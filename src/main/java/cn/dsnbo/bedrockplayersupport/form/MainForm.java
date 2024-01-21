package cn.dsnbo.bedrockplayersupport.form;

import cn.dsnbo.bedrockplayersupport.BasicPlugin;
import cn.dsnbo.bedrockplayersupport.BedrockPlayerSupport;
import cn.dsnbo.bedrockplayersupport.TeleportType;
import lombok.Getter;
import net.william278.huskhomes.BukkitHuskHomes;
import net.william278.huskhomes.user.OnlineUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.ModalForm;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author DongShaoNB
 */
public class MainForm {

    @Getter
    private static MainForm instance;

    public MainForm() {
        loadMainForm();
    }

    public void loadMainForm() {
        instance = this;
    }


    public void openBedrockTeleportForm(Player player) {
        UUID uuid = player.getUniqueId();
        List<String> onlinePlayerNameList = new ArrayList<>();
        if (BedrockPlayerSupport.getBasicPlugin() == BasicPlugin.HuskHomes && BedrockPlayerSupport.getMainConfigManager().getConfigData().enableCrossServer()) {
            BukkitHuskHomes bukkitHuskHomes = (BukkitHuskHomes) Bukkit.getPluginManager().getPlugin("Huskhomes");
            for (OnlineUser onlineUser: bukkitHuskHomes.getOnlineUsers()) {
                onlinePlayerNameList.add(onlineUser.getUsername());
            }
        } else {
            for (Player onlinePlayer: Bukkit.getOnlinePlayers()) {
                if (onlinePlayer != player) {
                    onlinePlayerNameList.add(onlinePlayer.getName());
                }
            }
        }
        CustomForm.Builder form = CustomForm.builder()
                .title("§l传送菜单")
                .dropdown("请选择传送类型", List.of("传送到对方的位置", "将对方传送到你的位置"))
                .dropdown("选择玩家", onlinePlayerNameList)
                .validResultHandler((customForm, customFormResponse) -> {
                    if (customFormResponse.asDropdown(0) == 0) {
                        player.chat("/tpa " + onlinePlayerNameList.get(customFormResponse.asDropdown(1)));
                    } else if (customFormResponse.asDropdown(0) == 1) {
                        player.chat("/tpahere " + onlinePlayerNameList.get(customFormResponse.asDropdown(1)));
                    }
                });
        BedrockPlayerSupport.getFloodgateApi().sendForm(uuid, form);
    }

    public void openBedrockTeleportHereForm(TeleportType tpType, Player requestor, Player receiver) {
        ModalForm.Builder form = null;
        String requestorName = requestor.getName();
        UUID receiverUuid = receiver.getUniqueId();
        if (tpType == TeleportType.Tpa) {
            form = ModalForm.builder()
                    .title("§l收到新的传送请求")
                    .content("玩家 " + requestorName + " 请求传送到你的位置")
                    .button1("§a同意")
                    .button2("§c拒绝")
                    .validResultHandler((modalForm, modalFormResponse) -> {
                        switch (modalFormResponse.clickedButtonId()) {
                            case 0 -> receiver.chat("/tpaccept");
                            case 1 -> receiver.chat("/tpdeny");
                        }
                    });
        } else if (tpType == TeleportType.TpaHere) {
            form = ModalForm.builder()
                    .title("§l收到新的传送请求")
                    .content("玩家 " + requestorName + " 请求把你传送到他的位置")
                    .button1("§a同意")
                    .button2("§c拒绝")
                    .validResultHandler((modalForm, modalFormResponse) -> {
                        switch (modalFormResponse.clickedButtonId()) {
                            case 0 -> receiver.chat("/tpaccept");
                            case 1 -> receiver.chat("/tpdeny");
                        }
                    });
        }
        BedrockPlayerSupport.getFloodgateApi().sendForm(receiverUuid, form);
    }

    public void openBedrockMsgForm(Player player) {
        List<String> onlinePlayerName = new ArrayList<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer != player) {
                onlinePlayerName.add(onlinePlayer.getName());
            }
        }
        CustomForm.Builder form = CustomForm.builder()
                .title("§l私信")
                .dropdown("请选择接收私信的玩家", onlinePlayerName)
                .input("请填写要发送的消息")
                .validResultHandler(((response, customFormResponse) -> {
                    player.chat("/msg " + onlinePlayerName.get(customFormResponse.asDropdown(0)) + " " + customFormResponse.asInput(1));
                }));
        BedrockPlayerSupport.getFloodgateApi().sendForm(player.getUniqueId(), form);
    }

    public void openBedrockBackForm(Player player) {
        ModalForm.Builder form = ModalForm.builder()
                .title("§l你死掉了")
                .content("是否要返回上个死亡点")
                .button1("§a是")
                .button2("§c否")
                .validResultHandler(((modalForm, modalFormResponse) -> {
                    switch (modalFormResponse.clickedButtonId()) {
                        case 0 -> player.chat("/back");
                    }
                }));
        BedrockPlayerSupport.getFloodgateApi().sendForm(player.getUniqueId(), form);
    }

}
