package com.cn.leedane.upload;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.ProgressListener;
import org.springframework.stereotype.Component;

@Component
public class FileUploadProgressListener implements ProgressListener {
	private HttpSession session;
    public void setSession(HttpSession session){
        this.session=session;
        Progress status = new Progress();//保存上传状态
        session.setAttribute("status", status);
    }
    /**
     * bytesRead 
     * 			The total number of bytes, which have been read so far.
     * contentLength 
     * 			The total number of bytes, which are being read. May be -1, if this number is unknown.
     * items 
     * 			The number of the field, which is currently being read. (0 = no item so far, 1 = first item is being read, ...)
     */
    @Override
    public void update(long bytesRead, long contentLength, int items) {
        Progress status = (Progress) session.getAttribute("status");
        status.setBytesRead(bytesRead);
        status.setContentLength(contentLength);
        status.setItems(items);
    }
}
