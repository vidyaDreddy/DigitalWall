

package com.digitalwall.services;

/*This interface should be implemented for every activities that uses REST API to download JSON Objects*/
public interface DownloadResult {

    void successDownload(String result);


}