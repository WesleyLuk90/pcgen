/*
 * derived from
 * CampaignSourceEntry.java
 * Copyright 2003 (C) David Hibbs <sage_sam@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.persistence.lst;

import java.net.URI;

import pcgen.base.lang.ObjectUtil;
import pcgen.util.Logging;

/**
 * A URIEntry contains a URI (either directly or indirectly via a URIFactory)
 * with a campaignName.
 */
public class URIEntry
{

	/**
	 * Contains the name of the Campaign in which this URIEntry is located
	 */
	private final String campaignName;

	/**
	 * Optionally contains the URIFactory used to determine the URI for this
	 * URIEntry. May be null if uri is set to a non-null value in the
	 * constructor.
	 */
	private final URIFactory uriFac;

	/**
	 * The URI for this URIEntry. May be null if this URIEntry contains a
	 * URIFactory. If so, it will be set to a non-null value when first
	 * required. May not be null if this URIEntry does not contain a URIFactory.
	 */
	private URI uri = null;

	/**
	 * Constructs a new URIEntry, directly specifying the URI.
	 * 
	 * @param campaignName
	 *            The Campaign name for this URIEntry
	 * @param uri
	 *            The URI for this URIEntry
	 */
	URIEntry(String campaignName, URI uri)
	{
		super();
		if (campaignName == null)
		{
			throw new IllegalArgumentException("Campaign Name can't be null");
		}
		if (uri == null)
		{
			throw new IllegalArgumentException("uri can't be null");
		}
		this.campaignName = campaignName;
		this.uri = uri;
		uriFac = null;
	}

	/**
	 * Constructs a new URIEntry with the URI defined by the given URIFactory.
	 * 
	 * The static getURIEntry method should be used to invoke this constructor
	 * and automatically build the URIFactory.
	 * 
	 * @param campaignName
	 *            The Campaign name for this URIEntry
	 * @param fac
	 *            The URIFactory for this URIEntry
	 */
	private URIEntry(String campaignName, URIFactory fac)
	{
		super();
		if (campaignName == null)
		{
			throw new IllegalArgumentException("campaignName can't be null");
		}
		if (fac == null)
		{
			throw new IllegalArgumentException("URI Factory can't be null");
		}
		this.campaignName = campaignName;
		this.uriFac = fac;
	}

	/**
	 * Returns the URI for this URIEntry.
	 * 
	 * Resolves the URI from the underlying URIFactory if required
	 * 
	 * @return The URI for this URIEntry
	 */
	public URI getURI()
	{
		if (uri == null)
		{
			uri = uriFac.getURI();
		}
		return uri;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object arg0)
	{
		if (arg0 == this)
		{
			return true;
		}
		if (arg0 instanceof URIEntry)
		{
			URIEntry other = (URIEntry) arg0;
			return ObjectUtil.compareWithNull(uriFac, other.uriFac)
				&& getURI().equals(other.getURI());
		}
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return this.getLSTformat().hashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder sBuff = new StringBuilder();
		sBuff.append("Campaign: ");
		sBuff.append(campaignName);
		sBuff.append("; SourceFile: ");
		sBuff.append(getURI());
		return sBuff.toString();
	}

	/**
	 * Returns a string representing the "original format" of this URIEntry. If
	 * the "original format" was a URI, then a String representing the URI is
	 * returned. If the "original format" was from a URIFactory, then the offset
	 * from the URIFactory is returned.
	 * 
	 * The offset is returned in the case of a URIFactory, since that is the
	 * "value" argument to the static getURIEntry method
	 * 
	 * @return A string representing the "original format" of this URIEntry
	 */
	public String getLSTformat()
	{
		if (uriFac == null)
		{
			return uri.toString();
		}
		else
		{
			return uriFac.getOffset();
		}
	}

	/**
	 * Returns a new URIEntry for a filename in the same directory as this
	 * URIEntry.
	 * 
	 * @param fileName
	 *            The file name that is in the same directory as the file from
	 *            this URIEntry.
	 * @return a new URIEntry for a filename in the same directory as this
	 *         URIEntry
	 */
	public URIEntry getRelatedTarget(String fileName)
	{
		if (uriFac == null)
		{
			throw new IllegalStateException(
				"getRelatedTarget can only be called on a URIEntry that uses a URI Factory");
		}
		return new URIEntry(campaignName, new URIFactory(uriFac.getRootURI(),
			fileName));
	}

	/**
	 * Constructs a new URIEntry from the given Campaign name, rootURI and
	 * offset.
	 * 
	 * @param campaignName
	 *            The Campaign name for this URIEntry
	 * @param rootURI
	 *            The root URI used as the starting point to determine the final
	 *            URI of the returned URIEntry
	 * @param offset
	 *            The offset from the root URI for the URIEntry
	 * @return A new URIEntry from the given Campaign name, rootURI and offset
	 */
	public static URIEntry getURIEntry(String campaignName, URI rootURI,
		String offset)
	{
		if (offset == null || offset.length() == 0)
		{
			Logging.errorPrint("Cannot build URIEntry for empty value in "
				+ rootURI);
			return null;
		}

		return new URIEntry(campaignName, new URIFactory(rootURI, offset));
	}
}