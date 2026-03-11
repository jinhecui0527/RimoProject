package com.hehe.rimo.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class RimoAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // TODO: Detect com.xingin.xhs (Xiaohongshu) or games
    }
    
    override fun onInterrupt() {
    }
}
