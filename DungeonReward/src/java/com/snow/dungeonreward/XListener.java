package java.com.snow.dungeonreward;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

import de.mickare.xserver.Message;
import de.mickare.xserver.XServerListener;
import de.mickare.xserver.annotations.XEventHandler;
import de.mickare.xserver.events.XServerMessageIncomingEvent;

public class XListener implements XServerListener {

	@XEventHandler(sync = false)
	public void onMessage(XServerMessageIncomingEvent event) {
		Message msg = event.getMessage();

		if (!msg.getSubChannel().startsWith("DXL")) return;

		DataInputStream in = new DataInputStream(new ByteArrayInputStream(msg.getContent()));
		String channel = msg.getSubChannel();

		try {
			if (channel.equals("DXL_HasReward")) {
				Reward.markReward(new UUID(in.readLong(), in.readLong()), in.readBoolean());
			} else if (channel.equals("DXL_Inv")) {
				UUID id = new UUID(in.readLong(), in.readLong());
				Reward reward = Reward.get(id);
				if (reward == null) {
					P.p.errorLog("Got Inv for non existent Player!");
					return;
				}
				if (!reward.createInv()) {
					P.p.errorLog("reward has got inv already");
					return;
				}

				reward.readInv(in);
				reward.sendGotInv(id);
			} else {
				P.p.errorLog("Ignoring unknown Message " + msg.getSubChannel());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
