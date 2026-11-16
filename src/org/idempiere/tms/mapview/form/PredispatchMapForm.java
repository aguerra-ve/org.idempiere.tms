package org.idempiere.tms.mapview.form;

import java.sql.SQLException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.HashSet;
import java.util.function.Function;
import java.util.logging.Level;

import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListCell;
import org.adempiere.webui.component.ListItem;
import org.adempiere.webui.component.Listbox;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.panel.IFormController;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.adempiere.webui.window.FDialog;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.idempiere.ui.zk.annotation.Form;
import org.idempiere.tms.mapview.map.MapProvider;
import org.idempiere.tms.mapview.map.MapLibreProvider;
import org.idempiere.tms.mapview.model.VehicleCandidate;
import org.idempiere.tms.mapview.repository.VehicleCandidateRepository;
import org.idempiere.tms.mapview.model.AgingBand;
import org.idempiere.tms.mapview.model.OrderCandidate;
import org.idempiere.tms.mapview.model.TemporaryRouteProposal;
import org.idempiere.tms.mapview.model.TemporaryProposalReviewer;
import org.idempiere.tms.mapview.repository.OrderCandidateRepository;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Center;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.North;
import org.zkoss.zul.South;
import org.zkoss.zul.Vlayout;

/** Native, read-only predispatch browser for iDempiere 12. */
@Form(name = "org.idempiere.tms.mapview.form.PredispatchMapForm")
public class PredispatchMapForm implements IFormController, EventListener<Event> {
	private static final CLogger LOGGER = CLogger.getCLogger(PredispatchMapForm.class);
	private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
			.withLocale(new Locale("es", "VE")).withZone(ZoneId.of("America/Caracas"));
	private static final String ON_MAP_SELECT = "onMapSelect";
	private static final String ON_MAP_AREA_SELECT = "onMapAreaSelect";

	private final CustomForm form = new CustomForm();
	private final Textbox search = new Textbox();
	private final Listbox stateFilter = new Listbox();
	private final Listbox municipalityFilter = new Listbox();
	private final Listbox parishFilter = new Listbox();
	private final Listbox warehouseFilter = new Listbox();
	private final Listbox orders = new Listbox();
	private final Listbox vehicleSelection = new Listbox();
	private final Label vehicleStatus = new Label();
	private final Iframe map = new Iframe();
	private final Button refresh = new Button("Actualizar");
	private final Button selectArea = new Button("Seleccionar por área");
	private final Button showSelection = new Button("Centrar selección en el mapa");
	private final Button reviewProposal = new Button("Revisar propuesta temporal");
	private final Label status = new Label();
	private final MapProvider provider = new MapLibreProvider();
	private List<OrderCandidate> candidates = List.of();
	private List<OrderCandidate> visibleCandidates = List.of();
	private final Set<Integer> selectedOrderIds = new HashSet<>();

	public PredispatchMapForm() {
		initializeUi();
		loadCandidates();
	}

	private void initializeUi() {
		form.setWidth("100%");
		form.setHeight("100%");
		Borderlayout layout = new Borderlayout();
		ZKUpdateUtil.setWidth(layout, "100%");
		ZKUpdateUtil.setHeight(layout, "100%");
		form.appendChild(layout);

		Vlayout top = new Vlayout();
		top.setSpacing("4px");
		top.setStyle("padding:6px 8px");
		top.appendChild(legend());
		top.appendChild(filters());
		top.appendChild(vehiclePicker());
		North north = new North();
		north.setBorder("none");
		north.appendChild(top);
		layout.appendChild(north);

		configureTable();
		map.setWidth("100%");
		map.setHeight("100%");
		map.setAutohide(true);
		map.addEventListener(ON_MAP_SELECT, this);
		map.addEventListener(ON_MAP_AREA_SELECT, this);
		installMapSelectionBridge();
		Center center = new Center();
		center.setBorder("none");
		center.appendChild(map);
		layout.appendChild(center);

		South south = new South();
		south.setBorder("none");
		south.setHeight("38%");
		south.setSplittable(true);
		south.setCollapsible(true);
		south.appendChild(orders);
		layout.appendChild(south);
	}

	private Vlayout legend() {
		Vlayout legend = new Vlayout();
		legend.setSpacing("2px");
		legend.setStyle("font-size:11px;line-height:16px;width:100%;overflow:hidden");
		Hlayout first = new Hlayout();
		first.setSpacing("8px");
		first.appendChild(legendItem(AgingBand.GREEN.getColor(), "Nuevo · menos de 24 h"));
		first.appendChild(legendItem(AgingBand.YELLOW.getColor(), "En atención · 24 a 47 h"));
		first.appendChild(legendItem(AgingBand.RED.getColor(), "Vencido · 48 h o más"));
		first.appendChild(legendItem("#9ca3af", "Despachado · 7 días · no seleccionable"));
		first.appendChild(legendItem("#f97316", "Borde naranja · entrega preparada"));
		Hlayout second = new Hlayout();
		second.setSpacing("8px");
		second.appendChild(styledLegend("◉ Seleccionado · borde azul", "color:#2563eb;font-weight:600"));
		second.appendChild(styledLegend("◎ Ubicación aproximada · punto por zona", "color:#64748b;font-weight:600"));
		second.appendChild(styledLegend("▱ Selección por área · arrastre con el mouse", "color:#334155;font-weight:600"));
		legend.appendChild(first);
		legend.appendChild(second);
		return legend;
	}

	private Label legendItem(String color, String text) {
		Label label = new Label("● " + text);
		label.setStyle("color:" + color + ";font-weight:600");
		return label;
	}

	private Label styledLegend(String text, String style) {
		Label label = new Label(text);
		label.setStyle(style);
		return label;
	}

	private Hlayout filters() {
		Hlayout bar = new Hlayout();
		bar.setSpacing("6px");
		bar.setStyle("display:flex;flex-wrap:wrap;align-items:center;row-gap:4px;width:100%");
		search.setPlaceholder("Pedido o cliente");
		search.setWidth("180px");
		configureFilter(stateFilter, "Todos los estados");
		configureFilter(municipalityFilter, "Todos los municipios");
		configureFilter(parishFilter, "Todas las parroquias");
		configureFilter(warehouseFilter, "Todos los almacenes");
		bar.appendChild(search);
		bar.appendChild(stateFilter);
		bar.appendChild(municipalityFilter);
		bar.appendChild(parishFilter);
		bar.appendChild(warehouseFilter);
		bar.appendChild(refresh);
		bar.appendChild(selectArea);
		bar.appendChild(showSelection);
		bar.appendChild(reviewProposal);
		bar.appendChild(status);
		search.addEventListener(Events.ON_CHANGE, this);
		search.addEventListener(Events.ON_OK, this);
		refresh.addActionListener(this);
		selectArea.addActionListener(this);
		showSelection.addActionListener(this);
		reviewProposal.addActionListener(this);
		reviewProposal.setDisabled(true);
		return bar;
	}

	private Hlayout vehiclePicker() {
		Hlayout bar = new Hlayout();
		bar.setSpacing("6px");
		bar.appendChild(new Label("Vehículo por placa:"));
		vehicleSelection.setMold("select");
		vehicleSelection.setWidth("300px");
		vehicleSelection.appendItem("Sin vehículo seleccionado", "");
		vehicleSelection.setSelectedIndex(0);
		bar.appendChild(vehicleSelection);
		bar.appendChild(vehicleStatus);
		return bar;
	}

	private void loadVehicles() {
		VehicleCandidate previous = selectedVehicle();
		vehicleSelection.removeAllItems();
		vehicleSelection.appendItem("Sin vehículo seleccionado", "");
		vehicleSelection.setSelectedIndex(0);
		try {
			List<VehicleCandidate> vehicles = new VehicleCandidateRepository(Env.getCtx()).findCandidates();
			for (VehicleCandidate vehicle : vehicles) {
				ListItem item = vehicleSelection.appendItem(vehicle.label(), "");
				item.setValue(vehicle);
				if (previous != null && previous.vehicleId() == vehicle.vehicleId()
						&& previous.plate().equals(vehicle.plate())) vehicleSelection.setSelectedItem(item);
			}
			vehicleStatus.setValue(vehicles.isEmpty() ? "Sin vehículos con placa disponibles para su rol"
					: "Selección temporal · cabida pendiente");
			vehicleSelection.setDisabled(vehicles.isEmpty());
		} catch (SQLException exception) {
			LOGGER.log(Level.WARNING, "Could not load authorized vehicles", exception);
			vehicleStatus.setValue("No se pudo consultar la flota; actualice el mapa");
			vehicleSelection.setDisabled(true);
		}
	}

	private VehicleCandidate selectedVehicle() {
		if (vehicleSelection.getSelectedItem() == null) return null;
		Object value = vehicleSelection.getSelectedItem().getValue();
		return value instanceof VehicleCandidate ? (VehicleCandidate)value : null;
	}

	private void configureFilter(Listbox filter, String emptyLabel) {
		filter.setWidth("165px");
		filter.setMold("select");
		filter.setRows(1);
		filter.setAttribute("emptyLabel", emptyLabel);
		filter.addEventListener(Events.ON_SELECT, this);
	}

	private void configureTable() {
		orders.setMultiple(true);
		orders.setCheckmark(true);
		orders.setVflex("1");
		orders.setWidth("100%");
		orders.setMold("paging");
		orders.setPageSize(25);
		orders.addEventListener(Events.ON_SELECT, this);
		Listhead head = new Listhead();
		head.setSizable(true);
		for (String title : List.of("Edad", "Pedido", "Cliente", "Estado", "Municipio", "Parroquia",
				"Almacén", "Pendiente", "Regla de entrega", "Completado")) {
			head.appendChild(new Listheader(title));
		}
		orders.appendChild(head);
	}

	private void loadCandidates() {
		loadVehicles();
		try {
			candidates = new OrderCandidateRepository(Env.getCtx()).findCandidates();
			Set<Integer> selectable = new HashSet<>();
			candidates.stream().filter(item -> !item.historical()).forEach(item -> selectable.add(item.orderId()));
			selectedOrderIds.retainAll(selectable);
			populateFilter(stateFilter, "Todos los estados", OrderCandidate::state);
			populateFilter(municipalityFilter, "Todos los municipios", OrderCandidate::municipality);
			populateFilter(parishFilter, "Todas las parroquias", OrderCandidate::parish);
			populateFilter(warehouseFilter, "Todos los almacenes", OrderCandidate::warehouseName);
			applyFilters();
		} catch (SQLException | RuntimeException e) {
			LOGGER.log(Level.SEVERE, "Could not load predispatch candidates", e);
			FDialog.error(0, form, "DBExecuteError", safeMessage(e));
		}
	}

	private void populateFilter(Listbox filter, String emptyLabel, Function<OrderCandidate, String> value) {
		filter.removeAllItems();
		filter.appendItem(emptyLabel, "");
		candidates.stream().map(value).filter(text -> text != null && !text.isBlank()).distinct()
				.sorted(String.CASE_INSENSITIVE_ORDER).forEach(text -> filter.appendItem(text, text));
		filter.setSelectedIndex(0);
	}

	private void applyFilters() {
		String needle = normalize(search.getValue());
		String state = selectedValue(stateFilter);
		String municipality = selectedValue(municipalityFilter);
		String parish = selectedValue(parishFilter);
		String warehouse = selectedValue(warehouseFilter);
		orders.getItems().clear();
		visibleCandidates = candidates.stream()
				.filter(item -> needle.isEmpty() || normalize(item.documentNo() + " " + item.customer()).contains(needle))
				.filter(item -> matches(state, item.state()))
				.filter(item -> matches(municipality, item.municipality()))
				.filter(item -> matches(parish, item.parish()))
				.filter(item -> matches(warehouse, item.warehouseName()))
			.sorted(Comparator.comparing(OrderCandidate::historical)
					.thenComparing(OrderCandidate::completedAt, Comparator.nullsLast(Comparator.naturalOrder())))
			.toList();
		visibleCandidates.forEach(this::appendRow);
		updateStatus();
		renderMap();
	}

	private void updateStatus() {
		long historical = candidates.stream().filter(OrderCandidate::historical).count();
		long missingLocation = candidates.stream().filter(item -> !item.hasCoordinates()
				&& org.idempiere.tms.mapview.model.StateCoordinates.find(item.state()) == null).count();
		status.setValue(visibleCandidates.size() + " de " + candidates.size() + " pedidos · "
				+ historical + " despachados · " + missingLocation + " sin ubicación · "
				+ selectedOrderIds.size() + " seleccionados");
		reviewProposal.setDisabled(selectedOrderIds.isEmpty());
	}

	private void appendRow(OrderCandidate candidate) {
		ListItem row = new ListItem();
		row.setValue(candidate);
		row.setSelected(!candidate.historical() && selectedOrderIds.contains(candidate.orderId()));
		row.setDisabled(candidate.historical());
		ListCell age = new ListCell(candidate.historical() ? "● Despachado" : "● " + candidate.agingBand().getLabel());
		age.setStyle("color:" + (candidate.historical() ? "#9ca3af" : candidate.agingBand().getColor())
				+ ";font-weight:600");
		row.appendChild(age);
		row.appendChild(new ListCell(candidate.documentNo()));
		row.appendChild(new ListCell(candidate.customer()
				+ (candidate.rma() ? " · RMA" : "")
				+ (candidate.specialAddress() ? " · dirección especial" : "")));
		row.appendChild(new ListCell(orMissing(candidate.state(), "Sin estado")));
		row.appendChild(new ListCell(orMissing(candidate.municipality(), "Sin municipio")));
		row.appendChild(new ListCell(orMissing(candidate.parish(), "Sin parroquia")
				+ (!candidate.hasCoordinates() ? " · ubicación aproximada/pendiente" : "")));
		row.appendChild(new ListCell(orMissing(candidate.warehouseName(), "Sin almacén")));
		row.appendChild(new ListCell(candidate.pendingQty().stripTrailingZeros().toPlainString()
				+ (candidate.prepared() ? " · entrega preparada" : "")));
		ListCell rule = new ListCell(candidate.deliveryRuleLabel());
		rule.setTooltiptext(candidate.deliveryRuleExplanation());
		row.appendChild(rule);
		row.appendChild(new ListCell(candidate.historical() && candidate.dispatchedAt() != null
				? DATE_TIME.format(candidate.dispatchedAt())
				: candidate.completedAt() == null ? "Fecha por conciliar" : DATE_TIME.format(candidate.completedAt())));
		orders.appendChild(row);
	}

	@Override
	public void onEvent(Event event) {
		if (event.getTarget().equals(refresh)) {
			loadCandidates();
		} else if (ON_MAP_SELECT.equals(event.getName())) {
			toggleMapSelection(event.getData());
		} else if (ON_MAP_AREA_SELECT.equals(event.getName())) {
			addMapAreaSelection(event.getData());
		} else if (event.getTarget().equals(selectArea)) {
			beginAreaSelection();
		} else if (event.getTarget().equals(showSelection)) {
			syncMapSelection(true);
		} else if (event.getTarget().equals(reviewProposal)) {
			reviewTemporaryProposal();
		} else if (event.getTarget().equals(orders)) {
			Set<Integer> visible = new HashSet<>();
			for (OrderCandidate item : visibleCandidates) if (!item.historical()) visible.add(item.orderId());
			selectedOrderIds.removeAll(visible);
			orders.getSelectedItems().stream().map(item -> (OrderCandidate) item.getValue())
					.filter(item -> !item.historical()).forEach(item -> selectedOrderIds.add(item.orderId()));
			syncMapSelection(false);
		} else {
			applyFilters();
		}
	}

	private void reviewTemporaryProposal() {
		if (selectedOrderIds.isEmpty()) {
			FDialog.warn(0, form, "NoRecordFound", "Seleccione al menos un pedido pendiente");
			return;
		}
		TemporaryRouteProposal proposal;
		try {
			proposal = TemporaryProposalReviewer.review(Env.getAD_Client_ID(Env.getCtx()),
					selectedOrderIds, new OrderCandidateRepository(Env.getCtx()), selectedVehicle(),
					new VehicleCandidateRepository(Env.getCtx())::findCandidates);
		} catch (SQLException exception) {
			LOGGER.log(Level.WARNING, "Could not refresh temporary proposal", exception);
			FDialog.warn(0, form, "Info", "No se pudo actualizar la propuesta. Actualice el mapa y vuelva a revisar.");
			return;
		}
		if (proposal.orders().isEmpty()) {
			FDialog.warn(0, form, "NoRecordFound", "Seleccione al menos un pedido pendiente");
			return;
		}
		FDialog.info(0, form, "Info", proposal.displayText(), "Propuesta temporal · no guardada");
	}

	private void renderMap() {
		String html = provider.createDocument(visibleCandidates, selectedOrderIds);
		setMapContent(html);
		map.invalidate();
	}

	private void installMapSelectionBridge() {
		String script = "var w=this,k='iqpMapHandler_" + map.getUuid()
				+ "';if(window[k])window.removeEventListener('message',window[k]);window[k]=function(e){"
				+ "if(!w.$n()||e.origin!==location.origin||e.source!==w.$n().contentWindow)return;var d=e.data;"
				+ "if(!d)return;if(d.type==='iqp-predispatch-toggle')zAu.send(new zk.Event(w,'" + ON_MAP_SELECT
				+ "',Number(d.orderId),{toServer:true}));else if(d.type==='iqp-predispatch-area')zAu.send(new zk.Event(w,'"
				+ ON_MAP_AREA_SELECT + "',String(d.orderIds||''),{toServer:true}));};window.addEventListener('message',window[k]);";
		map.setWidgetListener("onBind", script);
	}

	private void toggleMapSelection(Object value) {
		int orderId;
		try {
			orderId = value instanceof Number ? ((Number) value).intValue() : Integer.parseInt(String.valueOf(value));
		} catch (RuntimeException e) {
			LOGGER.warning("Invalid map selection value: " + value);
			return;
		}
		orders.getItems().stream().filter(item -> {
			OrderCandidate candidate = (OrderCandidate) item.getValue();
			return candidate.orderId() == orderId && !candidate.historical();
		}).findFirst().ifPresent(item -> {
			item.setSelected(!item.isSelected());
			if (item.isSelected()) selectedOrderIds.add(orderId);
			else selectedOrderIds.remove(orderId);
		});
		syncMapSelection(false);
	}

	private void addMapAreaSelection(Object value) {
		Set<Integer> ids = new java.util.HashSet<>();
		for (String token : String.valueOf(value == null ? "" : value).split(",")) {
			try {
				if (!token.isBlank()) ids.add(Integer.valueOf(token));
			} catch (NumberFormatException e) {
				LOGGER.warning("Invalid order ID from map area: " + token);
			}
		}
		orders.getItems().forEach(item -> {
			OrderCandidate candidate = (OrderCandidate) item.getValue();
			if (ids.contains(candidate.orderId()) && !candidate.historical()) {
				item.setSelected(true);
				selectedOrderIds.add(candidate.orderId());
			}
		});
		syncMapSelection(false);
	}

	private void beginAreaSelection() {
		String script = "var w=zk.Widget.$('#" + map.getUuid() + "');if(w&&w.$n()&&w.$n().contentWindow)"
				+ "w.$n().contentWindow.postMessage({type:'iqp-predispatch-area-start'},location.origin);";
		Clients.evalJavaScript(script);
	}

	private void syncMapSelection(boolean fit) {
		updateStatus();
		String ids = selectedOrderIds.stream().sorted().map(String::valueOf)
				.collect(java.util.stream.Collectors.joining(","));
		String script = "var w=zk.Widget.$('#" + map.getUuid() + "');if(w&&w.$n()&&w.$n().contentWindow)"
				+ "w.$n().contentWindow.postMessage({type:'iqp-predispatch-selection',orderIds:[" + ids
				+ "],fit:" + fit + "},location.origin);";
		Clients.evalJavaScript(script);
	}

	private void setMapContent(String html) {
		map.setContent(new AMedia("predespacho.html", "html", "text/html", html.getBytes(StandardCharsets.UTF_8)));
	}

	private static String selectedValue(Listbox filter) {
		if (filter.getSelectedItem() == null) {
			return "";
		}
		Object value = filter.getSelectedItem().getValue();
		return value == null ? "" : value.toString();
	}

	private static boolean matches(String filter, String value) {
		return filter.isEmpty() || filter.equals(value);
	}

	private static String normalize(String value) {
		return value == null ? "" : value.trim().toLowerCase(new Locale("es", "VE"));
	}

	private static String orMissing(String value, String fallback) {
		return value == null || value.isBlank() ? fallback : value;
	}

	private static String safeMessage(Throwable error) {
		return error.getMessage() == null ? error.getClass().getSimpleName() : error.getMessage();
	}

	@Override
	public ADForm getForm() {
		return form;
	}
}
