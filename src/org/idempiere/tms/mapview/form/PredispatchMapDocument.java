package org.idempiere.tms.mapview.form;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.idempiere.tms.mapview.model.OrderCandidate;
import org.idempiere.tms.mapview.model.StateCoordinates;

/** Builds the self-contained map document displayed by the ZK iframe. */
public final class PredispatchMapDocument {
	private static final String MAPLIBRE_JS = resource("/assets/maplibre/maplibre-gl.js").replace("</script", "<\\/script");
	private static final String MAPLIBRE_CSS = resource("/assets/maplibre/maplibre-gl.css");

	private PredispatchMapDocument() {
	}

	public static String create(List<OrderCandidate> candidates, Set<Integer> selected) {
		String features = candidates.stream().map(item -> feature(item, selected.contains(item.orderId())))
				.filter(value -> !value.isEmpty()).collect(Collectors.joining(","));
		String html = "<!doctype html><html><head><meta charset='utf-8'><meta name='viewport' content='width=device-width,initial-scale=1'>"
				+ "<style>" + MAPLIBRE_CSS + "html,body,#map{width:100%;height:100%;margin:0}body{font-family:Arial,sans-serif}"
				+ ".maplibregl-popup-content{border-radius:8px;box-shadow:0 4px 14px #0004;line-height:1.35;padding:10px 12px}"
				+ ".selection-box{position:absolute;z-index:20;border:2px solid #2563eb;background:rgba(37,99,235,.16);pointer-events:none}"
				+ ".area-help{position:absolute;z-index:20;top:10px;left:50%;transform:translateX(-50%);padding:7px 12px;border-radius:6px;background:#172033;color:#fff;font-size:12px;box-shadow:0 2px 8px #0005}</style></head>"
				+ "<body><div id='map'></div><script>" + MAPLIBRE_JS + "</script><script>"
				+ "const data={type:'FeatureCollection',features:[" + features + "]};"
				+ "const map=new maplibregl.Map({container:'map',style:'https://tiles.openfreemap.org/styles/liberty',bounds:[[-73.6,.6],[-59.5,12.8]],fitBoundsOptions:{padding:24},maxBounds:[[-75,-1],[-58,14]],minZoom:4,maxZoom:18});"
				+ "map.addControl(new maplibregl.NavigationControl(),'top-right');map.addControl(new maplibregl.ScaleControl({unit:'metric'}),'bottom-left');"
				+ "map.on('load',()=>{map.addSource('orders',{type:'geojson',data});map.addLayer({id:'orders',type:'circle',source:'orders',paint:{'circle-radius':['case',['get','selected'],11,8],'circle-color':['case',['get','historical'],'#9ca3af',['match',['get','band'],'GREEN','#16a34a','YELLOW','#eab308','RED','#dc2626','#64748b']],'circle-stroke-color':['case',['get','selected'],'#2563eb',['get','prepared'],'#f97316','#fff'],'circle-stroke-width':['case',['get','selected'],4,['get','prepared'],3,2]}});let popup,selecting=false,start,box,help,dragged=false;"
				+ "const show=(f,closeButton)=>{popup?.remove();const p=f.properties,box=document.createElement('div'),title=document.createElement('b');title.textContent=p.documentNo;box.appendChild(title);[p.customer,p.location,p.warehouse,'Pendiente: '+p.pending,p.prepared?'Entrega preparada':'',p.rma?'RMA':'',p.specialAddress?'Dirección especial':'',p.historical?'Despachado · no seleccionable':''].filter(Boolean).forEach(v=>{box.appendChild(document.createElement('br'));box.appendChild(document.createTextNode(v))});popup=new maplibregl.Popup({closeButton,closeOnClick:false}).setLngLat(f.geometry.coordinates).setDOMContent(box).addTo(map)};"
				+ "map.on('mouseenter','orders',e=>{map.getCanvas().style.cursor='pointer';show(e.features[0],false)});map.on('mouseleave','orders',()=>{map.getCanvas().style.cursor='';popup?.remove();popup=undefined});"
				+ "const point=e=>{const r=map.getCanvasContainer().getBoundingClientRect();return new maplibregl.Point(e.clientX-r.left,e.clientY-r.top)};"
				+ "const cancelArea=()=>{selecting=false;start=undefined;box?.remove();box=undefined;help?.remove();help=undefined;map.dragPan.enable();map.getCanvas().style.cursor=''};"
				+ "const beginArea=()=>{cancelArea();selecting=true;map.getCanvas().style.cursor='crosshair';help=document.createElement('div');help.className='area-help';help.textContent='Arrastre sobre los puntos · Esc cancela';map.getCanvasContainer().appendChild(help)};"
				+ "map.getCanvasContainer().addEventListener('mousedown',e=>{if(!selecting||e.button!==0)return;e.preventDefault();map.dragPan.disable();start=point(e);box=document.createElement('div');box.className='selection-box';map.getCanvasContainer().appendChild(box)});"
				+ "document.addEventListener('mousemove',e=>{if(!start||!box)return;const p=point(e),x=Math.min(start.x,p.x),y=Math.min(start.y,p.y);Object.assign(box.style,{transform:'translate('+x+'px,'+y+'px)',width:Math.abs(p.x-start.x)+'px',height:Math.abs(p.y-start.y)+'px'})});"
				+ "document.addEventListener('mouseup',e=>{if(!start)return;const end=point(e),features=map.queryRenderedFeatures([[Math.min(start.x,end.x),Math.min(start.y,end.y)],[Math.max(start.x,end.x),Math.max(start.y,end.y)]],{layers:['orders']}),ids=[...new Set(features.filter(f=>!f.properties.historical).map(f=>Number(f.properties.orderId)))];dragged=true;const chosen=new Set(ids);data.features.forEach(f=>{if(chosen.has(Number(f.properties.orderId)))f.properties.selected=true});map.getSource('orders').setData(data);parent.postMessage({type:'iqp-predispatch-area',orderIds:ids.join(',')},location.origin);cancelArea()});"
				+ "document.addEventListener('keydown',e=>{if(e.key==='Escape')cancelArea()});"
				+ "map.on('click','orders',e=>{if(dragged){dragged=false;return}const f=e.features[0];if((e.originalEvent.ctrlKey||e.originalEvent.metaKey)&&!f.properties.historical){f.properties.selected=!f.properties.selected;map.getSource('orders').setData(data);parent.postMessage({type:'iqp-predispatch-toggle',orderId:f.properties.orderId},location.origin);return}show(f,true)});"
				+ "window.addEventListener('message',e=>{if(e.origin!==location.origin||e.source!==parent)return;const d=e.data;if(!d)return;if(d.type==='iqp-predispatch-area-start'){beginArea();return}if(d.type!=='iqp-predispatch-selection')return;const ids=new Set(d.orderIds.map(Number));data.features.forEach(f=>f.properties.selected=!f.properties.historical&&ids.has(Number(f.properties.orderId)));map.getSource('orders')?.setData(data);if(d.fit&&ids.size){const b=new maplibregl.LngLatBounds();data.features.filter(f=>f.properties.selected).forEach(f=>b.extend(f.geometry.coordinates));if(!b.isEmpty())map.fitBounds(b,{padding:80,maxZoom:9})}});"
				+ "const chosen=data.features.filter(f=>f.properties.selected);if(chosen.length){const b=new maplibregl.LngLatBounds();chosen.forEach(f=>b.extend(f.geometry.coordinates));map.fitBounds(b,{padding:80,maxZoom:9});}});"
				+ "</script></body></html>";
		return html;
	}

	private static String feature(OrderCandidate item, boolean selected) {
		StateCoordinates.Point fallback = StateCoordinates.find(item.state());
		if (!item.hasCoordinates() && fallback == null) {
			return "";
		}
		double longitude = item.hasCoordinates() ? item.longitude() : fallback.longitude();
		double latitude = item.hasCoordinates() ? item.latitude() : fallback.latitude();
		if (!item.hasCoordinates()) {
			// Separate orders sharing a state centroid without claiming address-level accuracy.
			double angle = Math.floorMod(item.orderId() * 137, 360) * Math.PI / 180d;
			double radius = .04d + Math.floorMod(item.orderId(), 7) * .018d;
			longitude += Math.cos(angle) * radius;
			latitude += Math.sin(angle) * radius;
		}
		return "{type:'Feature',geometry:{type:'Point',coordinates:[" + longitude + "," + latitude
				+ "]},properties:{orderId:" + item.orderId() + ",documentNo:'" + js(item.documentNo()) + "',customer:'" + js(item.customer())
				+ "',location:'" + js(item.territorialLabel()) + "',pending:'"
				+ js(item.pendingQty().stripTrailingZeros().toPlainString()) + "',band:'"
				+ item.agingBand().name() + "',approximate:" + !item.hasCoordinates()
				+ ",warehouse:'" + js(item.warehouseName()) + "',rma:" + item.rma()
				+ ",specialAddress:" + item.specialAddress()
				+ ",historical:" + item.historical() + ",prepared:" + item.prepared()
				+ ",selected:" + (selected && !item.historical()) + "}}";
	}

	private static String js(String value) {
		return value == null ? "" : value.replace("\\", "\\\\").replace("'", "\\'")
				.replace("\r", " ").replace("\n", " ").replace("<", "\\u003c");
	}

	private static String resource(String name) {
		try (InputStream input = PredispatchMapDocument.class.getResourceAsStream(name)) {
			if (input == null) {
				throw new IllegalStateException("Missing bundle resource " + name);
			}
			return new String(input.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new IllegalStateException("Cannot read bundle resource " + name, e);
		}
	}
}
