package blue.language.feature;

import blue.language.model.Feature;

import java.util.Map;

public class BlueprintFeature implements Feature {
    private Map<String, Object> features;

    public BlueprintFeature(Map<String, Object> features) {
        this.features = features;
    }

    public Map<String, Object> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, Object> features) {
        this.features = features;
    }
}