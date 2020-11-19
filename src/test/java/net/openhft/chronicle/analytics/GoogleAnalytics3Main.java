package net.openhft.chronicle.analytics;

public class GoogleAnalytics3Main {


    public static void main(String[] args) {
        final Analytics analytics = Analytics.builder("UA-182232097-1", "testapp")
                .putEventParameter("app_version", "2.20.3-SNAPSHOT")
                .withReportDespiteJUnit()
                .build();

        analytics.sendEvent("started");

        try {
            Thread.sleep(10_000_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}