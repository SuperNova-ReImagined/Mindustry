package io.anuke.mindustry.ui.dialogs;

import io.anuke.arc.Core;
import io.anuke.arc.scene.ui.ScrollPane;
import io.anuke.arc.scene.ui.layout.Table;
import io.anuke.arc.util.*;
import io.anuke.mindustry.game.*;
import io.anuke.mindustry.maps.Map;
import io.anuke.mindustry.ui.BorderImage;

import static io.anuke.mindustry.Vars.*;

public class MapPlayDialog extends FloatingDialog{
    CustomRulesDialog dialog = new CustomRulesDialog();
    Rules rules;
    Gamemode selectedGamemode = Gamemode.survival;
    Map lastMap;

    public MapPlayDialog(){
        super("");
        setFillParent(false);

        onResize(() -> {
            if(lastMap != null){
                show(lastMap);
            }
        });
    }

    public void show(Map map){
        this.lastMap = map;
        title.setText(map.name());
        cont.clearChildren();

        //reset to any valid mode after switching to attack (one must exist)
        if(!selectedGamemode.valid(map)){
            selectedGamemode = Structs.find(Gamemode.all, m -> m.valid(map));
        }

        rules = map.rules();
        rules = selectedGamemode.apply(map.rules());

        Table selmode = new Table();
        selmode.add("$level.mode").colspan(4);
        selmode.row();
        int i = 0;

        Table modes = new Table();

        for(Gamemode mode : Gamemode.values()){
            if(mode.hidden) continue;

            modes.addButton(mode.toString(), "toggle", () -> {
                selectedGamemode = mode;
                rules = mode.apply(map.rules());
            }).update(b -> b.setChecked(selectedGamemode == mode)).size(140f, 54f).disabled(!mode.valid(map));
            if(i++ % 2 == 1) modes.row();
        }
        selmode.add(modes);
        selmode.addButton("?", this::displayGameModeHelp).width(50f).fillY().padLeft(18f);

        cont.add(selmode);
        cont.row();
        cont.addImageTextButton("$customize", "icon-tools-small", iconsizesmall, () -> dialog.show(rules, () -> rules = (selectedGamemode == null ? map.rules() : selectedGamemode.apply(map.rules())))).width(230);
        cont.row();
        cont.add(new BorderImage(map.texture, 3f)).size(mobile && !Core.graphics.isPortrait() ? 150f : 250f).get().setScaling(Scaling.fit);
        //only maps with survival are valid for high scores
        if(Gamemode.survival.valid(map)){
            cont.row();
            cont.label((() -> Core.bundle.format("level.highscore", map.getHightScore()))).pad(3f);
        }

        buttons.clearChildren();
        addCloseButton();

        buttons.addImageTextButton("$play", "icon-play", iconsize, () -> {
            control.playMap(map, rules);
            hide();
            ui.custom.hide();
        }).size(210f, 64f);

        show();
    }

    private void displayGameModeHelp(){
        FloatingDialog d = new FloatingDialog(Core.bundle.get("mode.help.title"));
        d.setFillParent(false);
        Table table = new Table();
        table.defaults().pad(1f);
        ScrollPane pane = new ScrollPane(table);
        pane.setFadeScrollBars(false);
        table.row();
        for(Gamemode mode : Gamemode.values()){
            if(mode.hidden) continue;
            table.labelWrap("[accent]" + mode.toString() + ":[] [lightgray]" + mode.description()).width(400f);
            table.row();
        }

        d.cont.add(pane);
        d.buttons.addButton("$ok", d::hide).size(110, 50).pad(10f);
        d.show();
    }
}
