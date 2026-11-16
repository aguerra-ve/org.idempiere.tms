package org.idempiere.tms.component;

import java.util.List;

import org.adempiere.plugin.utils.Incremental2PackActivator;
import org.compiere.model.MForm;
import org.compiere.model.MFormAccess;
import org.compiere.model.MRole;
import org.compiere.model.Query;
import org.compiere.util.CLogger;
import org.compiere.util.Env;

/** Installs TMS dictionary packs and grants read-only access to the dispatch form. */
public class TMSActivator extends Incremental2PackActivator {
	private static final CLogger LOGGER = CLogger.getCLogger(TMSActivator.class);
	private static final String FORM_UUID = "7d83e978-12ed-4048-a1c2-6eb964766c69";
	private static final String DISPATCH_ROLE_UUID = "a262ac5c-3c37-41e5-8dcb-dd46028bdb7a";

	@Override
	protected void afterPackIn() {
		MForm form = new Query(Env.getCtx(), MForm.Table_Name, "AD_Form_UU=?", null)
				.setParameters(FORM_UUID).first();
		MRole role = new Query(Env.getCtx(), MRole.Table_Name, "AD_Role_UU=?", null)
				.setParameters(DISPATCH_ROLE_UUID).first();
		if (form == null) {
			LOGGER.warning("Predispatch form not found; access was not configured");
			return;
		}
		List<MFormAccess> grants = new Query(Env.getCtx(), MFormAccess.Table_Name,
				"AD_Form_ID=?", null).setParameters(form.getAD_Form_ID()).list();
		for (MFormAccess granted : grants) {
			boolean allowed = granted.getAD_Role_ID() == 0
					|| (role != null && granted.getAD_Role_ID() == role.getAD_Role_ID());
			if (granted.isActive() != allowed || granted.isReadWrite()) {
				granted.setIsActive(allowed);
				granted.setIsReadWrite(false);
				granted.saveEx();
			}
		}
		if (role == null) {
			LOGGER.warning("Dispatch role not found; only System Administrator can access the form");
			return;
		}
		MFormAccess access = new Query(Env.getCtx(), MFormAccess.Table_Name,
				"AD_Form_ID=? AND AD_Role_ID=?", null)
				.setParameters(form.getAD_Form_ID(), role.getAD_Role_ID()).first();
		if (access == null) access = new MFormAccess(form, role.getAD_Role_ID());
		access.setIsActive(true);
		access.setIsReadWrite(false);
		access.saveEx();
	}
}
