package com.mitrais.more.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadFileResponse {
	
	private String fileName;
	private String fileType;
	private long size;
	
}
