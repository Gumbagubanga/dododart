systemd-analyze time
systemd-analyze critical-chain

systemctl disable rpi-eeprom-update
apt purge triggerhappy* vlc* cups* thonny geany modemmanager bluez ca-certificates* -y
apt install dnsmasq hostapd fonts-noto-color-emoji unclutter ca-certificates-java -y
apt install openjdk-17-jdk-headless -y
apt upgrade -y
apt autoremove -y

curl -O https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh
chmod +x wait-for-it.sh

tee <<EOF /etc/systemd/system/dodo.service >/dev/null
[Unit]
Description=Dodo service
After=network.target
StartLimitIntervalSec=0

[Service]
Type=simple
Restart=always
RestartSec=1
User=dodo
ExecStart=/usr/bin/env java -jar /home/dodo/dododart-1.0.0.jar

[Install]
WantedBy=multi-user.target
EOF
systemctl enable dodo

echo "@unclutter" >> /etc/xdg/lxsession/LXDE-pi/autostart
echo "@xset s off" >> /etc/xdg/lxsession/LXDE-pi/autostart
echo "@xset -dpms" >> /etc/xdg/lxsession/LXDE-pi/autostart
echo "@xset s noblank" >> /etc/xdg/lxsession/LXDE-pi/autostart
echo "@/home/dodo/wait-for-it.sh localhost:8080 -- chromium-browser http://localhost:8080
--start-fullscreen --kiosk --incognito --autoplay-policy=no-user-gesture-required --noerrdialogs
--no-first-run --disk-cache-dir=/dev/null" >> /etc/xdg/lxsession/LXDE-pi/autostart

# https://www.raspberrypi.com/documentation/computers/configuration.html#setting-up-a-routed-wireless-access-point

mv /etc/dnsmasq.conf /etc/dnsmasq.conf.orig

echo "interface wlan0" >> /etc/dhcpcd.conf
echo "static ip_address=10.88.0.1/24" >> /etc/dhcpcd.conf
echo "nohook wpa_supplicant" >> /etc/dhcpcd.conf

echo "interface=wlan0" >> /etc/dnsmasq.conf
echo "dhcp-range=10.88.0.2,10.88.0.20,255.255.255.0,24h" >> /etc/dnsmasq.conf
echo "address=/gw.wlan/10.88.0.1" >> /etc/dnsmasq.conf

rfkill unblock wlan

tee <<EOF /etc/hostapd/hostapd.conf >/dev/null
country_code=DE
interface=wlan0
ssid=DodoWifi
hw_mode=g
channel=7
macaddr_acl=0
auth_algs=1
ignore_broadcast_ssid=0
wpa=2
wpa_passphrase=Q1w2e3r4t5
wpa_key_mgmt=WPA-PSK
wpa_pairwise=TKIP
rsn_pairwise=CCMP
EOF

chmod 600 /etc/hostapd/hostapd.conf

hostapd -dd /etc/hostapd/hostapd.conf

systemctl unmask hostapd
systemctl enable hostapd
