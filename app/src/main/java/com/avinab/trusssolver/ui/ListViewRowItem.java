package com.avinab.trusssolver.ui;

/**
 * Avinab
 * 10/12/2014.
 */
public class ListViewRowItem
{
	private int imageId;
	private String title;

	public ListViewRowItem(int imageId, String title)
	{
		this.imageId = imageId;
		this.title = title;
	}

	public int getImageId()
	{
		return imageId;
	}

	public void setImageId(int imageId)
	{
		this.imageId = imageId;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	@Override
	public String toString()
	{
		return title;
	}

}
