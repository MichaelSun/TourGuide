package com.find.guide.api.invite;

import com.find.guide.api.base.PMRequestBase;
import com.plugin.internet.core.annotations.NeedTicket;
import com.plugin.internet.core.annotations.OptionalParam;
import com.plugin.internet.core.annotations.RequiredParam;
import com.plugin.internet.core.annotations.RestMethodUrl;

@NeedTicket
@RestMethodUrl("inviteEvent/setSatisfaction")
public class SetSatisfactionRequest extends
		PMRequestBase<SetSatisfactionResponse> {

	@RequiredParam("eventId")
	private long eventId;

	@RequiredParam("guideId")
	private int guideId;

	@RequiredParam("satisfaction")
	private int satisfaction;

	@OptionalParam("saContent")
	private String saContent;

	public SetSatisfactionRequest(long eventId, int guideId, int satisfaction,
			String saContent) {
		this.eventId = eventId;
		this.guideId = guideId;
		this.satisfaction = satisfaction;
		this.saContent = saContent;
	}

}
