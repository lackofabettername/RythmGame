package misccelaneous;

import imgui.ImGui;

abstract public class GUIWindow {
    public final String ID, Title;

    public GUIWindow(String ID) {
        this.ID = ID;
        Title = ID;
    }

    public GUIWindow(String ID, String title) {
        this.ID = ID;
        Title = title;
    }

    //This SHOULD be package private
    void renderProper() {
        ImGui.begin(Title);
        render();
        ImGui.end();
    }

    public abstract void render();
}
