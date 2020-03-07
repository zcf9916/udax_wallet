package com.github.wxiaoqi.security.common.util.model;

import java.io.Serializable;

public interface Config extends Serializable {
	static final String FILE_DEFAULT_WIDTH = "120";
	static final String FILE_DEFAULT_HEIGHT = "120";
	static final String FILE_DEFAULT_AUTHOR = "wallet";

	static final String PROTOCOL = "http://";
	static final String SEPARATOR = "/";

	static final String TRACKER_NGNIX_PORT = "8080";
}
