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
/** Generated Model - DO NOT CHANGE */
package org.idempiere.tms.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Model for DD_ProductNesting
 *  @author Adempiere (generated)
 *  @version Release 3.9.0 - $Id$ */
public class X_DD_ProductNesting extends PO implements I_DD_ProductNesting, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20260728L;

    /** Standard Constructor */
    public X_DD_ProductNesting (Properties ctx, int DD_ProductNesting_ID, String trxName)
    {
      super (ctx, DD_ProductNesting_ID, trxName);
      /** if (DD_ProductNesting_ID == 0)
        {
			setClassification (null);
			setDD_ProductNesting_ID (0);
			setNestingFamily (null);
			setSeqNo (0);
        } */
    }

    /** Load Constructor */
    public X_DD_ProductNesting (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuffer sb = new StringBuffer ("X_DD_ProductNesting[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Classification.
		@param Classification
		Model code — must match M_Product.Classification (e.g. TLL-0370). One row per model, not per color variant.
	  */
	public void setClassification (String Classification)
	{
		set_Value (COLUMNNAME_Classification, Classification);
	}

	/** Get Classification.
		@return Model code — must match M_Product.Classification (e.g. TLL-0370). One row per model, not per color variant.
	  */
	public String getClassification ()
	{
		return (String)get_Value(COLUMNNAME_Classification);
	}

	/** Set Product Nesting.
		@param DD_ProductNesting_ID Product Nesting	  */
	public void setDD_ProductNesting_ID (int DD_ProductNesting_ID)
	{
		if (DD_ProductNesting_ID < 1)
			set_ValueNoCheck (COLUMNNAME_DD_ProductNesting_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_DD_ProductNesting_ID, Integer.valueOf(DD_ProductNesting_ID));
	}

	/** Get Product Nesting.
		@return Product Nesting	  */
	public int getDD_ProductNesting_ID ()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_DD_ProductNesting_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Description.
		@param Description
		Optional short description of the record
	  */
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription ()
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** Set Nested Volume.
		@param NestedVolume
		Marginal volume this model contributes when nested inside any larger sibling of the same NestingFamily.
		PENDING MEASUREMENT — not yet based on real data. Treat as 0/NULL until measured; do not assume a value.
	  */
	public void setNestedVolume (BigDecimal NestedVolume)
	{
		set_Value (COLUMNNAME_NestedVolume, NestedVolume);
	}

	/** Get Nested Volume.
		@return Marginal volume this model contributes when nested inside any larger sibling of the same NestingFamily.
		PENDING MEASUREMENT — not yet based on real data. Treat as 0/NULL until measured; do not assume a value.
	  */
	public BigDecimal getNestedVolume ()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_NestedVolume);
		if (bd == null)
			 return BigDecimal.ZERO;
		return bd;
	}

	/** Set Nesting Family.
		@param NestingFamily
		Group of sizes that can nest with each other (e.g. CILINDRICO, CONICO). Any smaller SeqNo can nest inside any larger SeqNo of the same family — not only adjacent sizes.
	  */
	public void setNestingFamily (String NestingFamily)
	{
		set_Value (COLUMNNAME_NestingFamily, NestingFamily);
	}

	/** Get Nesting Family.
		@return Group of sizes that can nest with each other (e.g. CILINDRICO, CONICO). Any smaller SeqNo can nest inside any larger SeqNo of the same family — not only adjacent sizes.
	  */
	public String getNestingFamily ()
	{
		return (String)get_Value(COLUMNNAME_NestingFamily);
	}

	/** Set Sequence.
		@param SeqNo
		Ascending size rank within the NestingFamily. Not required to be consecutive or unique.
	  */
	public void setSeqNo (int SeqNo)
	{
		set_Value (COLUMNNAME_SeqNo, Integer.valueOf(SeqNo));
	}

	/** Get Sequence.
		@return Ascending size rank within the NestingFamily. Not required to be consecutive or unique.
	  */
	public int getSeqNo ()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_SeqNo);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair()
    {
        return new KeyNamePair(get_ID(), getClassification());
    }

	/** Set Immutable Universally Unique Identifier.
		@param UUID
		Immutable Universally Unique Identifier
	  */
	public void setUUID (String UUID)
	{
		set_Value (COLUMNNAME_UUID, UUID);
	}

	/** Get Immutable Universally Unique Identifier.
		@return Immutable Universally Unique Identifier
	  */
	public String getUUID ()
	{
		return (String)get_Value(COLUMNNAME_UUID);
	}
}
