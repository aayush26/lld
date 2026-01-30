public interface SettingCommand {
    void execute();
    void undo();
    void redo();
    boolean isUndoable();
}