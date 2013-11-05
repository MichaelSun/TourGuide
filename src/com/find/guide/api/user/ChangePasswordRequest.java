package com.find.guide.api.user;

import com.find.guide.api.base.PMRequestBase;
import com.plugin.internet.core.annotations.NeedTicket;
import com.plugin.internet.core.annotations.RequiredParam;
import com.plugin.internet.core.annotations.RestMethodUrl;

@NeedTicket
@RestMethodUrl("user/changePassword")
public class ChangePasswordRequest extends
		PMRequestBase<ChangePasswordResponse> {

	@RequiredParam("oldPassword")
	private String oldPwd;

	@RequiredParam("newPassword")
	private String newPwd;

	public ChangePasswordRequest(String oldPwd, String newPwd) {
		this.oldPwd = oldPwd;
		this.newPwd = newPwd;
	}

}
