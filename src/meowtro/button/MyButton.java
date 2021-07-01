package meowtro.button;

import javafx.scene.control.Button;

public abstract class MyButton {
    protected int cost;
    protected Button btn;
    public Button getButton() {
        return this.btn;
    }
    public abstract void onClick();
}
