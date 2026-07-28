# Delta
Delta was a backdoor plugin I created in mid-2023 to specifically grant the bot I was using on TotalFreedom and TotalFreedom: Rebooted the ability to collect player coordinates despite patches made to the Scissors fork of Paper.

## How does it work?
Delta is an exceptionally simple plugin. It uses the ProtocolLib API to intercept entity query packets (the type of query that gets sent when you press F3+I while looking at an entity player) if the person sending the packet either has a specific permission node or matches one of my Minecraft accounts. The behavior beyond this point depends on what the client requested from the server.

If the server sends a packet with the transaction ID of 20101111 (when TotalFreedom was created) and the entity ID of 20140324 (when I applied for Super Admin status), the plugin responds with an NBT compound of player UUIDs and their corresponding integer IDs that is internally named `DeltaEntityIDs`. Otherwise, the server will respond with a very barebones response only containing their coordinate data along with the world they are located in.

## License
As this plugin has no purpose outside of historic TotalFreedom use, I have decided to license it under the CC0-1.0 license, effectively making it public domain. Have fun.
