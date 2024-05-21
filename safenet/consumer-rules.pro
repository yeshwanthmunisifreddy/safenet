
# Keep public classes in the com.thesubgraph.safenet package.
-keep class com.thesubgraph.safenet.NetworkMonitor{ *; }
-keep class com.thesubgraph.safenet.AccessToken{ *; }
-keep class com.thesubgraph.safenet.SessionState{ *; }
-keep class com.thesubgraph.safenet.data.common.** { *; }
-keep class com.thesubgraph.safenet.data.serialization.** { *; }
-keep class com.thesubgraph.safenet.SessionState.** { *; }
-keep class com.thesubgraph.safenet.di.DefaultDispatcher{ *; }
-keep class com.thesubgraph.safenet.di.IoDispatcher{ *; }
-keep class com.thesubgraph.safenet.di.MainDispatcher{ *; }
-keep class com.thesubgraph.safenet.di.DispatcherModule{ *; }
-keep class com.thesubgraph.safenet.di.AppModule{ *; }
-keep class com.thesubgraph.safenet.di.RetrofitModule{ *; }
-keep, allowobfuscation class com.thesubgraph.safenet.di.** { *; }

-keepclasseswithmembers,includedescriptorclasses class * {
   @dagger.* <fields>;
}

