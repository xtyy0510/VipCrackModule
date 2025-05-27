提供一下最初测试的frida进行hook的版本

viphook_EN.js

```js
Java.perform(function() {
    // Get the UserInfo class
    var UserInfo = Java.use('com.hcn.mm.beans.UserInfo');

    // Hook the constructor to modify properties after creation
    UserInfo.$init.overload(
        'java.lang.String', 'java.lang.String', 'java.lang.String', 'java.lang.String', 
        'java.lang.String', 'java.lang.String', 'int', 'int', 'int', 'java.lang.String', 
        'java.lang.String', 'int', 'java.lang.String', 'int', 'int', 'long', 
        'java.lang.Integer', 'java.lang.Double', 'java.lang.Double', 'java.lang.Double', 
        'java.lang.Double', 'java.lang.String', 'java.lang.String', 'java.lang.Integer', 
        'java.lang.String', 'java.lang.Integer'
    ).implementation = function(
        id, openId, nickName, headPic, invitationCode, anotherInvitationCode, 
        remainCount, userStatus, vipStatus, vipStartDate, vipEndDate, loginType, 
        uniqueUuid, countDay, bindingStatus, vipAdLastTime, inviteCount, 
        inviteIncomePending, inviteIncomeReady, inviteIncomeGet, inviteIncomeTaken, 
        inviteCode, inviteUrl, invitedPayedCount, invitedBy, bindPlatformType
    ) {
        // Call original constructor
        var result = this.$init(
            id || "default_user_id", 
            openId || "default_open_id", 
            nickName || "Frida VIP User", 
            headPic || "https://example.com/avatar.jpg", 
            invitationCode || "FRIDA123", 
            anotherInvitationCode, 
            999, // Set high remain count
            1, // Active user status
            2, // Set to VIP status (2 for infinity VIP)
            "2023-01-01", // VIP start date
            "2099-12-31", // VIP end date
            1, // Login type (1 for WeChat)
            uniqueUuid || "frida-generated-uuid",
            36500, // Count day (100 years)
            1, // Binding status (1 for bound)
            Date.now(), // VIP ad last time
            Java.use('java.lang.Integer').valueOf(999), // Invite count
            Java.use('java.lang.Double').valueOf(999.99), // Income pending
            Java.use('java.lang.Double').valueOf(999.99), // Income ready
            Java.use('java.lang.Double').valueOf(999.99), // Income get
            Java.use('java.lang.Double').valueOf(999.99), // Income taken
            "FRIDA123", // Invite code
            "https://example.com/invite", // Invite URL
            Java.use('java.lang.Integer').valueOf(999), // Invited paid count
            "admin", // Invited by
            Java.use('java.lang.Integer').valueOf(1) // Bind platform type
        );
        
        console.log("[+] Modified UserInfo object created");
        console.log("    Nickname: " + this.getNickName());
        console.log("    VIP Status: " + this.getVipStatus());
        console.log("    Is VIP: " + this.isVip());
        console.log("    Is Infinity VIP: " + this.isInfinityVip());
        
        return result;
    };

    // Also hook the isVip() method to always return true as a backup
    UserInfo.isVip.implementation = function() {
        console.log("[*] isVip() called - forcing true");
        return true;
    };

    // Hook isInfinityVip() to always return true
    UserInfo.isInfinityVip.implementation = function() {
        console.log("[*] isInfinityVip() called - forcing true");
        return true;
    };

    console.log("[*] UserInfo class hooked successfully");
});
```

frida启动命令

```powershell
frida -U -l viphook_EN.js -f com.hcn.mm
```

然后游客登录修改水印相机，vip就会自动生效

![QQ_1748347677746](C:\Users\xtyy\AppData\Local\Temp\QQ_1748347677746.png)
