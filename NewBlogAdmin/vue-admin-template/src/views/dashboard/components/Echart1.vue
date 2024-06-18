<template>
  <div ref="chart" style="width: 600px; height: 400px;"></div>
</template>

<script>
// 引入 ECharts 主模块
import * as echarts from 'echarts/core';
// 引入柱状图图表，图表后缀都为 Chart
import { LineChart } from 'echarts/charts';
// 引入提示框和标题组件，组件后缀都为 Component
import { TitleComponent, TooltipComponent, GridComponent } from 'echarts/components';
// 引入 Canvas 渲染器，注意引入 CanvasRenderer 或者 SVGRenderer 是必须的一步
import { CanvasRenderer } from 'echarts/renderers';

// 注册必须的组件
echarts.use(
  [TitleComponent, TooltipComponent, GridComponent, LineChart, CanvasRenderer]
);

export default {
  name: 'LineChart',
  data() {
    return {
      chart: null 
    };
  },
  props: {
    x_data: Array,
    y_data: Array,
    color: String,
    title:String
  },
  mounted() {
    this.initChart();
  },
  methods: {
    // 初始化表
    initChart() {
      const chartDom = this.$refs.chart;
      this.chart = echarts.init(chartDom);
      this.updateChart()
    },
    // 重绘表
    updateChart(){
      const option = {
        title: {
          text: this.title,
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: this.x_data
        },
        yAxis: {
          type: 'value'
        },
        series: [{
          data: this.y_data,
          type: 'line',
          smooth: true,
          areaStyle: {
            color: this.color  // 半透明的区域填充颜色
          },
        }]
      };
      this.chart.setOption(option);
    }
  },
  beforeDestroy() {
    if (this.chart) {
      this.chart.dispose();
    }
  },

  watch: {
    x_data: function() {
      this.updateChart();
    },
    y_data: function() {
      this.updateChart();
    },
    color: function() {
      this.updateChart();
    },
    title: function() {
      this.updateChart();
    }
  },
}
</script>

<style scoped>
/* 可以添加一些 CSS 样式 */
.my3{
  background-color: rgba(10, 87, 196, 0.545);
}
</style>