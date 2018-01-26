package workshop.akbolatss.greendaogenerator;


import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Property;
import org.greenrobot.greendao.generator.PropertyType;
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
        addNotification(schema);

        Property dataIdForInfo = info.addLongProperty("dataId").notNull().getProperty();
        Property infoIdForData = data.addLongProperty("infoId").notNull().getProperty();
        Property dataIdForCharts = charts.addLongProperty("dataId").notNull().getProperty();

        info.addToOne(data, dataIdForInfo, "data"); // one-to-one (data.getInfo)
        data.addToOne(info, infoIdForData, "info");

        ToMany dataIdToCharts = data.addToMany(charts, dataIdForCharts);
        dataIdToCharts.setName("charts");
//        Property infoProp = info.addLongProperty("dataId").notNull().getProperty();
//        Property dataProp = data.addLongProperty("infoId").getProperty();
//        data.addToOne(info, infoProp, "info");

//        Property chartsProp = charts.addLongProperty("dataId").notNull().getProperty();
//        data.addToMany(charts, chartsProp, "charts");
    }


    private static Entity addChartResponseData(final Schema schema) {
        Entity data = schema.addEntity("ChartData");
        data.addIdProperty().primaryKey().autoincrement();
        data.addStringProperty("exchange");
        data.addStringProperty("coin");
        data.addStringProperty("currency");
        data.addStringProperty("source");
        data.addBooleanProperty("isActive");
        data.addStringProperty("timing");
        data.addBooleanProperty("isLoading");
        return data;
    }

    private static Entity addChartResponseDataInfo(final Schema schema) {
        Entity info = schema.addEntity("ChartDataInfo");
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
        Entity chart = schema.addEntity("ChartDataCharts");
        chart.addIdProperty().primaryKey().autoincrement();
        chart.addFloatProperty("market");
        chart.addStringProperty("high");
        chart.addStringProperty("low");
        chart.addStringProperty("price");
        chart.addLongProperty("timestamp");
        return chart;
    }

    private static Entity addNotification(final Schema schema) {
        Entity notification = schema.addEntity("Notification");
        notification.addIdProperty().primaryKey().autoincrement();
        notification.addStringProperty("name");
        notification.addIntProperty("hour");
        notification.addIntProperty("minute");
        notification.addBooleanProperty("isActive");
        return notification;
    }
}
