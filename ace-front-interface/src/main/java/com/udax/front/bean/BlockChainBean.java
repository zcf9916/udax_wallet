package com.udax.front.bean;

import java.io.Serializable;
import java.util.List;

public class BlockChainBean implements Serializable{

	/**
	 * 区块链平台参数接收对象
	 */
	private static final long serialVersionUID = 1L;

	private List<String> userAddress;//接收区块链平台推送的用户批量地址

	private String symbol;

	private String proxyCode; //平台标识

	public void setProxyCode(String proxyCode){
		this.proxyCode=proxyCode;
	}
	public String getProxyCode(){
		return proxyCode;
	}

	public List<String> getUserAddress() {
		return userAddress;
	}

	public void setUserAddress(List<String> userAddress) {
		this.userAddress = userAddress;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

}
