public class SettingManager {
    private final Map<String, Setting<?>> settings = new HashMap<>();
    private List<SettingObservers> observers = new ArrayList<>();
    private final CommandManager CommandManager;
    private final PolicyService policyService;

    public SettingManager(CommandManager commandManager, PolicyService policyService){
        this.commandManager = commandManager;
        this.policyService = policyService;
    }

    public void registerSetting(Setting<?> setting){
        settings.put(setting.getId(), setting);
    }

    public <T> void updateSetting(String id, T value) {
        // TODO
        notifyObservers(id);
    }

    public void addObserver(SettingObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(String id) {
        for(SettingObserver observer:observers) {
            observer.onSettingChanged(id);
        }
    }
}