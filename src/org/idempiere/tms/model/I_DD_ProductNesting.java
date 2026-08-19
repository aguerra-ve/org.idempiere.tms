/******************************************************************************
 * Product: ADempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 2006-2017 ADempiere Foundation, All Rights Reserved.         *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * or (at your option) any later version.										*
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * or via info@adempiere.net or http://www.adempiere.net/license.html         *
 *****************************************************************************/
package org.idempiere.tms.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Interface for DD_ProductNesting
 *  @author Adempiere (generated)
 *  @version Release 3.9.0
 */
public interface I_DD_ProductNesting
{

    /** TableName=DD_ProductNesting */
    public static final String Table_Name = "DD_ProductNesting";

    /** AD_Table_ID=0 (assigned by the Application Dictionary when the table is registered) */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 3 - Client - Org
     */
    BigDecimal accessLevel = BigDecimal.valueOf(3);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Client.
	  * Client/Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/** Set Organization.
	  * Organizational entity within client
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within client
	  */
	public int getAD_Org_ID();

    /** Column name Classification */
    public static final String COLUMNNAME_Classification = "Classification";

	/** Set Classification.
	  * Model code — must match M_Product.Classification (e.g. TLL-0370). One row per model, not per color variant.
	  */
	public void setClassification (String Classification);

	/** Get Classification.
	  * Model code — must match M_Product.Classification (e.g. TLL-0370). One row per model, not per color variant.
	  */
	public String getClassification();

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created.
	  * Date this record was created
	  */
	public Timestamp getCreated();

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();

    /** Column name DD_ProductNesting_ID */
    public static final String COLUMNNAME_DD_ProductNesting_ID = "DD_ProductNesting_ID";

	/** Set Product Nesting	  */
	public void setDD_ProductNesting_ID (int DD_ProductNesting_ID);

	/** Get Product Nesting	  */
	public int getDD_ProductNesting_ID();

    /** Column name Description */
    public static final String COLUMNNAME_Description = "Description";

	/** Set Description.
	  * Optional short description of the record
	  */
	public void setDescription (String Description);

	/** Get Description.
	  * Optional short description of the record
	  */
	public String getDescription();

    /** Column name IsActive */
    public static final String COLUMNNAME_IsActive = "IsActive";

	/** Set Active.
	  * The record is active in the system
	  */
	public void setIsActive (boolean IsActive);

	/** Get Active.
	  * The record is active in the system
	  */
	public boolean isActive();

    /** Column name NestedVolume */
    public static final String COLUMNNAME_NestedVolume = "NestedVolume";

	/** Set Nested Volume.
	  * Marginal volume this model contributes when nested inside any larger sibling of the same NestingFamily.
	  * PENDING MEASUREMENT — not yet based on real data. Treat as 0/NULL until measured; do not assume a value.
	  */
	public void setNestedVolume (BigDecimal NestedVolume);

	/** Get Nested Volume.
	  * Marginal volume this model contributes when nested inside any larger sibling of the same NestingFamily.
	  * PENDING MEASUREMENT — not yet based on real data. Treat as 0/NULL until measured; do not assume a value.
	  */
	public BigDecimal getNestedVolume();

    /** Column name NestingFamily */
    public static final String COLUMNNAME_NestingFamily = "NestingFamily";

	/** Set Nesting Family.
	  * Group of sizes that can nest with each other (e.g. CILINDRICO, CONICO). Any smaller SeqNo can nest inside any larger SeqNo of the same family — not only adjacent sizes.
	  */
	public void setNestingFamily (String NestingFamily);

	/** Get Nesting Family.
	  * Group of sizes that can nest with each other (e.g. CILINDRICO, CONICO). Any smaller SeqNo can nest inside any larger SeqNo of the same family — not only adjacent sizes.
	  */
	public String getNestingFamily();

    /** Column name SeqNo */
    public static final String COLUMNNAME_SeqNo = "SeqNo";

	/** Set Sequence.
	  * Ascending size rank within the NestingFamily. Not required to be consecutive or unique.
	  */
	public void setSeqNo (int SeqNo);

	/** Get Sequence.
	  * Ascending size rank within the NestingFamily. Not required to be consecutive or unique.
	  */
	public int getSeqNo();

    /** Column name UUID */
    public static final String COLUMNNAME_UUID = "UUID";

	/** Set Immutable Universally Unique Identifier.
	  * Immutable Universally Unique Identifier
	  */
	public void setUUID (String UUID);

	/** Get Immutable Universally Unique Identifier.
	  * Immutable Universally Unique Identifier
	  */
	public String getUUID();

    /** Column name Updated */
    public static final String COLUMNNAME_Updated = "Updated";

	/** Get Updated.
	  * Date this record was updated
	  */
	public Timestamp getUpdated();

    /** Column name UpdatedBy */
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";

	/** Get Updated By.
	  * User who updated this records
	  */
	public int getUpdatedBy();
}
