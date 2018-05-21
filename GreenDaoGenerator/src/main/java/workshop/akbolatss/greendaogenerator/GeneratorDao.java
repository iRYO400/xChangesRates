package workshop.akbolatss.greendaogenerator;


import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Property;
import org.greenrobot.greendao.generator.Schema;
import org.greenrobot.greendao.generator.ToMany;

public class GeneratorDao {

    public static void main(String[] arg) {
        Schema schema = new Schema(1, "workshop.akbolatss.xchangesrates.model"); // Your app package name and the (.db) is the folder where the DAO files will be generated into.
        schema.enableKeepSectionsByDefault();

        addTables(schema);

        try {
            new DaoGenerator().generateAll(schema, "src/main/java");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addTables(final Schema schema) {
        Entity data = addChartResponseData(schema);
        Entity info = addChartResponseDataInfo(schema);
        Entity charts = addChartResponseDataChart(schema);
        Entity notifications = addNotification(schema);
        addGlobalNotification(schema);

        Property dataIdForInfo = info.addLongProperty("snapshotId").notNull().getProperty();
        Property infoIdForData = data.addLongProperty("infoId").notNull().getProperty();
        Property dataIdForCharts = charts.addLongProperty("snapshotId").notNull().getProperty();

        Property dataIdForNotifications = notifications.addLongProperty("snapshotId").notNull().getProperty();

        info.addToOne(data, dataIdForInfo, "snapshot"); // one-to-one (data.getInfo)
        data.addToOne(info, infoIdForData, "info");

        ToMany dataIdToCharts = data.addToMany(charts, dataIdForCharts);
        dataIdToCharts.setName("charts");

        ToMany dataIdToNotifications = data.addToMany(notifications, dataIdForNotifications);
        dataIdToNotifications.setName("notifications");
//        Property infoProp = info.addLongProperty("dataId").notNull().getProperty();
//        Property dataProp = data.addLongProperty("infoId").getProperty();
//        data.addToOne(info, infoProp, "info");

//        Property chartsProp = charts.addLongProperty("dataId").notNull().getProperty();
//        data.addToMany(charts, chartsProp, "charts");
    }


    private static Entity addChartResponseData(final Schema schema) {
        Entity data = schema.addEntity("Snapshot");
        data.addIdProperty().primaryKey().autoincrement();
        data.addStringProperty("exchange");
        data.addStringProperty("coin");
        data.addStringProperty("currency");
        data.addStringProperty("source");
        data.addBooleanProperty("isActiveForGlobal"); // For Global Notifications
        data.addStringProperty("timing");
        data.addBooleanProperty("isIntervalEnabled"); // Update interval
        data.addIntProperty("IntervalNumber"); // 0-59 for second, minutes, hours; 0-365 for days
        data.addIntProperty("IntervalType"); // 0 - second, 1 - minutes, 2 - hours, 3 - days
        data.addBooleanProperty("isNotifyPersistent"); // Show persistent notification for Interval updates
        return data;
    }

    private static Entity addChartResponseDataInfo(final Schema schema) {
        Entity info = schema.addEntity("SnapshotInfo");
        info.addIdProperty().primaryKey().autoincrement();
        info.addFloatProperty("volume");
        info.addStringProperty("high");
        info.addFloatProperty("change24");
        info.addStringProperty("last");
        info.addStringProperty("low");
        info.addStringProperty("buy");
        info.addStringProperty("sell");
        info.addFloatProperty("change");
        info.addStringProperty("started");
        info.addStringProperty("multiply");
        info.addStringProperty("updated");
        info.addLongProperty("timestamp");
        return info;
    }

    private static Entity addChartResponseDataChart(final Schema schema) {
        Entity chart = schema.addEntity("SnapshotChart");
        chart.addIdProperty().primaryKey().autoincrement();
        chart.addFloatProperty("market");
        chart.addStringProperty("high");
        chart.addStringProperty("low");
        chart.addStringProperty("price");
        chart.addLongProperty("timestamp");
        return chart;
    }

    private static Entity addNotification(final Schema schema) {
        Entity notification = schema.addEntity("SnapshotNotification");
        notification.addIdProperty().primaryKey().autoincrement();
        notification.addBooleanProperty("isActive");
        notification.addStringProperty("name"); // 21:30, 06:00, 12:00
        notification.addIntProperty("hour"); // between 0-23
        notification.addIntProperty("minutes"); // between 0-59
        notification.addIntProperty("ConditionIndex"); // 0 = Percent Change, 1 = Percent Change Up, etc
        notification.addIntProperty("ConditionValue"); // +- 3%, +- 50%
        notification.addBooleanProperty("isVibrateOn"); // Vibration
        notification.addBooleanProperty("isSoundOn"); // Sound
        notification.addBooleanProperty("isLedOn"); // LED
        return notification;
    }

    private static Entity addGlobalNotification(final Schema schema) {
        Entity notification = schema.addEntity("GlobalNotification");
        notification.addIdProperty().primaryKey().autoincrement();
        notification.addBooleanProperty("isActive");
        notification.addStringProperty("name"); // 21:30, 06:00, 12:00
        notification.addIntProperty("hour"); // between 0-23
        notification.addIntProperty("minutes"); // between 0-59
        notification.addIntProperty("ConditionIndex"); // 0 = Percent Change, 1 = Percent Change Up, etc
        notification.addIntProperty("ConditionValue"); // +- 3%, +- 50%
        notification.addBooleanProperty("isVibrateOn"); // Vibration
        notification.addBooleanProperty("isSoundOn"); // Sound
        notification.addBooleanProperty("isLedOn"); // LED
        return notification;
    }
}
