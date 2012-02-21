# key and keystore password for Garnett is RumGrumble
# 
# keystore password for GarnettClient.keystore is LondonStrumps
# 

doit:
	echo key and keystore password for VanHorne is RumGrumble
	echo keystore password for VanHorne_Client.keystore is LondonStrumps
	keytool -genkey -alias GarnettServer -keyalg RSA -keystore GarnettServer.keystore
	keytool -list -v -keystore GarnettServer.keystore
	keytool -export -alias GarnettServer -keystore GarnettServer.keystore -rfc -file GarnettServer.cer
	keytool -import -alias GarnettServer -file GarnettServer.cer -keystore GarnettClient.keystore
	keytool -list -v -keystore GarnettClient.keystore
