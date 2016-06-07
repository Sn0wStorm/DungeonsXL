package java.com.snow.dungeonreward;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

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
				String player = in.readUTF();
				Reward.markReward(player, in.readBoolean());
			} else if (channel.equals("DXL_Inv")) {
				String player = in.readUTF();

				Reward reward = Reward.get(player);
				if (reward == null) {
					P.p.errorLog("Got Inv for non existent Player!");
					return;
				}
				if (!reward.createInv()) {
					P.p.errorLog("reward has got inv already");
					return;
				}

				reward.readInv(in);
				reward.sendGotInv(player);
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
