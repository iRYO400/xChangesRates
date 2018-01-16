package workshop.akbolatss.greendaogenerator;


import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Property;
import org.greenrobot.greendao.generator.Schema;

public class GeneratorDao {

    private static final String PROJECT_DIR = System.getProperty("user.dir");

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

        Property infoProp = info.addLongProperty("infoId").notNull().getProperty();
        data.addToOne(info, infoProp);

        Property chartsProp = charts.addLongProperty("chartsId").notNull().getProperty();
        data.addToMany(charts, chartsProp, "charts");
    }


    private static Entity addChartResponseData(final Schema schema) {
        Entity data = schema.addEntity("ChartData");
        data.addIdProperty().primaryKey().autoincrement();
        data.addStringProperty("exchange");
        data.addStringProperty("coin");
        data.addStringProperty("currency");
        data.addStringProperty("source");
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
}
