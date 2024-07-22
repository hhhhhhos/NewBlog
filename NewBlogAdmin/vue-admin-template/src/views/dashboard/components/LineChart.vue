<template>
  <div ref="chart" :class="className" :style="{height:height,width:width}" />
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
import { debounce } from 'lodash'; // 必须要括号 {} 才能部分导出

// 注册必须的组件
echarts.use(
    [TitleComponent, TooltipComponent, GridComponent, LineChart, CanvasRenderer]
);
require('echarts/theme/macarons') // echarts theme

export default {
    props: {
        className: {
            type: String,
            default: 'chart'
        },
        width: {
            type: String,
            default: '100%'
        },
        height: {
            type: String,
            default: '350px'
        },
        autoResize: {
            type: Boolean,
            default: true
        },
        chartData: {
            type: Object,
            required: true
        }
    },
    data() {
        return {
            chart: null,
            throttledResize: null  // 存储节流函数引用
        }
    },
    watch: {
        chartData: {
            deep: true,
            handler(val) {
                this.setOptions(val)
            }
        }
    },
    mounted() {
        this.$nextTick(() => {
            this.initChart()
        })

        // 防抖 最后一次动作200秒后触发 方法不能带括号
        this.throttledResize = debounce(this.redraw, 200);


        // 添加事件监听器
        window.addEventListener('resize', this.throttledResize);

    },
    beforeDestroy() {
        if (!this.chart) {
            return
        }
        this.chart.dispose()
        this.chart = null
        window.removeEventListener('resize', this.throttledResize);
    },
    methods: {
        initChart() {
            const chartDom = this.$refs.chart;
            this.chart = echarts.init(chartDom);
            this.setOptions(this.chartData)

            // 添加点击事件监听器
            this.chart.on('click', (params) => {
                if (params.componentType === 'series') {
                    console.log('点击了图表的点:', params);
                    this.handlePointClick(params); // 调用点点击处理函数
                }
            });

        },
        handlePointClick(params) {
            // 这里是点击点后要执行的逻辑
            console.log(`你点击了 ${params.name} 的点`);
            this.$emit('PointClick',params.dataIndex)
            // 你可以在这里添加更多的逻辑，如显示详细信息，打开模态窗口等
        },
        setOptions({ actualData } = {}) {

            console.log("!!!!!!"+this.chartData.name )
       
            this.chart.setOption({
                xAxis: {
                    data: this.getLast7Days(),
                    boundaryGap: false,
                    axisTick: {
                        show: false
                    }
                },
                grid: {
                    left: 20,
                    right: 25,
                    bottom: 20,
                    top: 30,
                    containLabel: true
                },
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'cross'
                    },
                    padding: [5, 10]
                },
                yAxis: {
                    axisTick: {
                        show: false
                    }
                },
                legend: {
                    data: [ this.chartData.name]
                },
                series: [
                    {
                        name: this.chartData.name,
                        smooth: true,
                        type: 'line',
                        itemStyle: {
                            normal: {
                                color: this.chartData.color,
                                lineStyle: {
                                    color: this.chartData.color,
                                    width: 2
                                },
                                areaStyle: {
                                    color: '#f3f8ff'
                                }
                            }
                        },
                        data: actualData,
                        animationDuration: 2800,
                        animationEasing: 'quadraticOut'
                    }]
            })
        },
        getLast7Days() {
            const dates = [];
            for (let i = 6; i >= 0; i--) {
                const date = new Date();
                date.setDate(date.getDate() - i); // 回溯到过去的第 i 天
                const formattedDate = `${date.getMonth() + 1}月${date.getDate()}日`; // 格式化日期
                dates.push(formattedDate);
            }
            return dates;
        },
        redraw(){
            this.chart.resize()
            console.log('窗口大小改变了,防抖重绘:', window.innerWidth, window.innerHeight);
        }
    }
}
</script>

<style>

</style>
