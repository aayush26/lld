interface Setting <T> {
    String getId();
    T getValue();
    void setValue(T value);
    boolean validate(T value);
    ApplyPolicy getApplyPolicy();
    boolean isUndoable();
}