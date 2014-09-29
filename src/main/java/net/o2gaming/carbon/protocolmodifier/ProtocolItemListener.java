package net.o2gaming.carbon.protocolmodifier;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import net.minecraft.server.v1_7_R4.Item;
import net.minecraft.server.v1_7_R4.WatchableObject;
import net.o2gaming.carbon.Carbon;

public class ProtocolItemListener {

	private Carbon plugin;

	public ProtocolItemListener(Carbon plugin) {
		this.plugin = plugin;
	}

	private int[] replacements = new int[4096];
	{
		for (int i = 0; i < replacements.length; i++) {
			replacements[i] = -1;
		}
		//TODO: all materials
		//slime -> emerald block
		replacements[165] = 133;
		//barrier -> ? (this is totally needed here to avoid client crash, the question is should we use glass or bedrock) (stone as of now)
		replacements[166] = 1;
		//iron trapdoor -> trapdoor
		replacements[167] = 96;
		//prismarine -> mossy cobblestone
		replacements[168] = 48;
		//sea lantern -> glowstone
		replacements[169] = 89;
		//red sandstone -> sandstone
		replacements[179] = 24;
		//red sandstone stairs -> sandstone stairs
		replacements[180] = 128;
		//red sandstone doubleslab -> double step
		replacements[181] = 43;
		//red sandstone slab -> step
		replacements[182] = 44;
		//all fence gates -> fence gate
		replacements[183] = 107;
		replacements[184] = 107;
		replacements[185] = 107;
		replacements[186] = 107;
		replacements[187] = 107;
		//all fences -> fence
		replacements[188] = 85;
		replacements[189] = 85;
		replacements[190] = 85;
		replacements[191] = 85;
		replacements[192] = 85;
		//all doors -> door
		replacements[427] = 64;
		replacements[428] = 64;
		replacements[429] = 64;
		replacements[430] = 64;
		replacements[431] = 64;
		//everything else -> stone as of now
		replacements[409] = 1;
		replacements[410] = 1;
		replacements[411] = 1;
		replacements[412] = 1;
		replacements[413] = 1;
		replacements[414] = 1;
		replacements[415] = 1;
		replacements[416] = 1;
		replacements[423] = 1;
		replacements[424] = 1;
	}

	public void init() {
		ProtocolLibrary.getProtocolManager().addPacketListener(
			new PacketAdapter(
				PacketAdapter.params(plugin, PacketType.Play.Server.WINDOW_ITEMS)
			) {
				@SuppressWarnings("deprecation")
				@Override
				public void onPacketSending(PacketEvent event) {
					if (ProtocolLibrary.getProtocolManager().getProtocolVersion(event.getPlayer()) == 47) {
						return;
					}
					//replace all items with valid ones
					ItemStack[] items = event.getPacket().getItemArrayModifier().read(0);
					for (int i = 0; i < items.length; i++) {
						if (items[i] == null) {
							continue;
						}
						int itemid = items[i].getTypeId();
						if (replacements[itemid] != -1) {
							items[i].setTypeId(replacements[itemid]);
						}
					}
				}
			}
		);

		ProtocolLibrary.getProtocolManager().addPacketListener(
			new PacketAdapter(
				PacketAdapter.params(plugin, PacketType.Play.Server.SET_SLOT)
			) {
				@SuppressWarnings("deprecation")
				@Override
				public void onPacketSending(PacketEvent event) {
					if (ProtocolLibrary.getProtocolManager().getProtocolVersion(event.getPlayer()) == 47) {
						return;
					}
					//replace item with valid one
					ItemStack item = event.getPacket().getItemModifier().read(0);
					if (item == null) {
						return;
					}
					int itemid = item.getTypeId();
					if (replacements[itemid] != -1) {
						item.setTypeId(replacements[itemid]);
					}
				}
			}
		);

		ProtocolLibrary.getProtocolManager().addPacketListener(
			new PacketAdapter(
				PacketAdapter.params(plugin, PacketType.Play.Server.ENTITY_EQUIPMENT)
			) {
				@SuppressWarnings("deprecation")
				@Override
				public void onPacketSending(PacketEvent event) {
					if (ProtocolLibrary.getProtocolManager().getProtocolVersion(event.getPlayer()) == 47) {
						return;
					}
					//replace item valid one
					ItemStack item = event.getPacket().getItemModifier().read(0);
					if (item == null) {
						return;
					}
					int itemid = item.getTypeId();
					if (replacements[itemid] != -1) {
						item.setTypeId(replacements[itemid]);
					}
				}
			}
		);

		ProtocolLibrary.getProtocolManager().addPacketListener(			
			new PacketAdapter(
				PacketAdapter.params(plugin, PacketType.Play.Server.ENTITY_METADATA)
			) {
				@Override
				public void onPacketSending(PacketEvent event) {
					if (ProtocolLibrary.getProtocolManager().getProtocolVersion(event.getPlayer()) == 47) {
						return;
					}
					//create a new packet with valid items and send it (Had to do this because metadata packets are shared)
					event.setCancelled(true);
					PacketContainer newpacket = event.getPacket().deepClone();
					List<?> list = newpacket.getSpecificModifier(List.class).read(0);
					for (Object object : list) {
						WatchableObject wobject = (WatchableObject) object;
						if (wobject.b() instanceof net.minecraft.server.v1_7_R4.ItemStack) {
							net.minecraft.server.v1_7_R4.ItemStack itemStack = (net.minecraft.server.v1_7_R4.ItemStack) wobject.b();
							int itemid = Item.getId(itemStack.getItem());
							if (replacements[itemid] != -1) {
								itemStack.setItem(Item.getById(replacements[itemid]));
							}
						}
					}
					try {
						ProtocolLibrary.getProtocolManager().sendServerPacket(event.getPlayer(), newpacket, false);
					} catch (InvocationTargetException e) {
					}
				}
			}
		);
	}

}