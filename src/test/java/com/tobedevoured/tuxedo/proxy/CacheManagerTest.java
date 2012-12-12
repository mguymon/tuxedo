package com.tobedevoured.tuxedo.proxy;

import static org.junit.Assert.*;

import org.junit.Test;

import com.tobedevoured.tuxedo.proxy.CacheManager;

public class CacheManagerTest {

    @Test
    public void extensionCheck() {
        assertTrue( "should match .png", CacheManager.extensions.matcher("/assets/subscribe.png").matches() );
        assertFalse( "should not match without an extension", CacheManager.extensions.matcher("/assetspng").matches() );
    }
}
