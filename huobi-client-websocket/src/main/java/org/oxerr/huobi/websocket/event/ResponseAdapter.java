package org.oxerr.huobi.websocket.event;

import org.oxerr.huobi.websocket.dto.response.Response;
import org.oxerr.huobi.websocket.dto.response.payload.Payload;

/**
 * An abstract adapter class for receiving Huobi exchange events. The methods in
 * this class are empty. This class exists as convenience for creating listener
 * objects.
 */
public abstract class ResponseAdapter implements ResponseListener {

	@Override
	public void onResponse(Response<? extends Payload> response) {
	}

}
