//package com.spacedlearning.config.cache;
//
//import java.time.Duration;
//import java.util.Arrays;
//
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.cache.caffeine.CaffeineCacheManager;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//
//import com.github.benmanes.caffeine.cache.Caffeine;
//
//@Configuration
//@EnableCaching
//public class CacheConfig {
//
//	private static final String CACHE_MANAGER = "CACHE_MANAGER";
//	private static final String PUBLIC_MODULES_CACHE_MANAGER = "PUBLIC_MODULES_CACHE_MANAGER";
//	private static final String RECENT_MODULES_CACHE_MANAGER = "RECENT_MODULES_CACHE_MANAGER";
//
//	@Primary
//	@Bean(name = CACHE_MANAGER)
//	CacheManager cacheManager() {
//        final CaffeineCacheManager cacheManager = new CaffeineCacheManager();
//
//        // Configure cache names`
//        cacheManager
//            .setCacheNames(
//						Arrays.asList("publicModules", "recentModules", "moduleStatistics", "folders",
//								"vocabularyCounts", "books", "modules", "bookCategories", "userModuleProgress",
//								"moduleProgress"));
//
//        // Default cache configuration
//        cacheManager.setCaffeine(caffeineCacheBuilder());
//
//        return cacheManager;
//    }
//
//    @Bean
//	Caffeine<Object, Object> caffeineCacheBuilder() {
//        return Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(30)).maximumSize(1000);
//    }
//
//    /**
//     * Custom cache configurations can be defined as separate cache managers if
//     * needed
//     */
//	@Bean(name = PUBLIC_MODULES_CACHE_MANAGER)
//	CacheManager publicModulesCacheManager() {
//        final CaffeineCacheManager cacheManager = new CaffeineCacheManager("publicModules");
//        cacheManager.setCaffeine(Caffeine.newBuilder().expireAfterWrite(Duration.ofHours(1)).maximumSize(200));
//        return cacheManager;
//    }
//
//	@Bean(name = RECENT_MODULES_CACHE_MANAGER)
//	CacheManager recentModulesCacheManager() {
//        final CaffeineCacheManager cacheManager = new CaffeineCacheManager("recentModules");
//        cacheManager.setCaffeine(Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(15)).maximumSize(500));
//        return cacheManager;
//    }
//}