package com.perseverance.phando.factory;

import com.perseverance.phando.FeatureConfigClass;

/**
 * Created by TrilokiNath on 05-12-2017.
 */

public class FeatureConfigFactory {

    public static FeatureConfigInterface getFeatureConfigInterfaceInstance() {
        return new FeatureConfigClass();
    }

}
