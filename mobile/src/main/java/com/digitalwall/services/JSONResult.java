

package com.digitalwall.services;

/*This interface should be implemented for every activities that uses REST API to download JSON Objects*/
public interface JSONResult {

    void successJsonResult(int code, Object result);

    void failedJsonResult(int code);
}