package com.ly.musicplay.bean;

public class Music {
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArrtist() {
		return arrtist;
	}

	public void setArrtist(String arrtist) {
		this.arrtist = arrtist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Music(long id, String title, String arrtist, String album,
			long size, long duration, String url, String name) {
		super();
		this.id = id;
		this.title = title;
		this.arrtist = arrtist;
		this.album = album;
		this.size = size;
		this.duration = duration;
		this.url = url;
		this.name = name;
	}

	public Music() {
		super();
	}

	private long id;

	public Music(long id, String title, String arrtist, String album,
			String albumId, long size, long duration, String url, String name) {
		super();
		this.id = id;
		this.title = title;
		this.arrtist = arrtist;
		this.album = album;
		this.albumId = albumId;
		this.size = size;
		this.duration = duration;
		this.url = url;
		this.name = name;
	}

	private String title;
	private String arrtist;
	private String album;
	private String albumId;
	private long size;
	private long duration;
	private String url;
	private String name;

	public String getAlbumId() {
		return albumId;
	}

	public void setAlbumId(String albumId) {
		this.albumId = albumId;
	}

}
