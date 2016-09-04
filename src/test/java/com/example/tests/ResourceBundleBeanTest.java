package com.example.tests;

import com.example.component.ResourceBundleBean;
import com.example.view.LocaleModel;
import org.junit.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class ResourceBundleBeanTest {
    @Test
    public void equals() throws Exception {
        ResourceBundleBean resourceBundleBean = new ResourceBundleBean(null, null);

        assertThat(resourceBundleBean).isEqualTo(resourceBundleBean)
                .isEqualTo(new ResourceBundleBean(null, null));
    }

    @Test
    public void notEquals() throws Exception {
        assertThat(new ResourceBundleBean(new LocaleModel(null), null))
                .isNotEqualTo(new ResourceBundleBean(null, null))
                .isNotEqualTo(null)
                .isNotEqualTo(new HashMap<>());
    }

}
