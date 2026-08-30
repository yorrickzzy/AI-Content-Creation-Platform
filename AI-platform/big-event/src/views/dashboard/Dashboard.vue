<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { Document, EditPen, View, TrendCharts } from '@element-plus/icons-vue'
import { dashboardCategoryDistributionService, dashboardHotArticlesService, dashboardOverviewService, dashboardPublishTrendService } from '@/api/article.js'

const overview = ref({ articleCount: 0, draftCount: 0, publishedCount: 0, totalViews: 0 })
const hotArticles = ref([])
const trendElement = ref(null)
const categoryElement = ref(null)
let trendChart
let categoryChart

const chartBase = { textStyle: { fontFamily: 'Microsoft YaHei' }, tooltip: { trigger: 'axis' } }
const loadDashboard = async () => {
    const [overviewResult, trendResult, categoryResult, hotResult] = await Promise.all([dashboardOverviewService(), dashboardPublishTrendService(), dashboardCategoryDistributionService(), dashboardHotArticlesService()])
    overview.value = overviewResult.data
    hotArticles.value = hotResult.data
    trendChart = echarts.init(trendElement.value)
    trendChart.setOption({ ...chartBase, grid: { left: 34, right: 20, top: 34, bottom: 24 }, xAxis: { type: 'category', data: trendResult.data.map(item => item.date?.slice(5)) }, yAxis: { type: 'value', minInterval: 1 }, series: [{ type: 'line', smooth: true, data: trendResult.data.map(item => item.count), areaStyle: { color: '#b6dfc6' }, lineStyle: { color: '#168054', width: 3 }, itemStyle: { color: '#168054' } }] })
    categoryChart = echarts.init(categoryElement.value)
    categoryChart.setOption({ ...chartBase, tooltip: { trigger: 'item' }, legend: { bottom: 0 }, series: [{ type: 'pie', radius: ['46%', '70%'], data: categoryResult.data.map(item => ({ name: item.categoryName, value: item.articleCount })), label: { formatter: '{b}\n{c}' }, color: ['#168054', '#e28a45', '#397a94', '#9a5f72', '#b1a13f'] }] })
}
const resizeCharts = () => { trendChart?.resize(); categoryChart?.resize() }
onMounted(async () => { await loadDashboard(); window.addEventListener('resize', resizeCharts) })
onBeforeUnmount(() => { window.removeEventListener('resize', resizeCharts); trendChart?.dispose(); categoryChart?.dispose() })
</script>

<template>
    <div class="dashboard-page">
        <div class="heading"><div><p>CONTENT OVERVIEW</p><h1>数据看板</h1></div><span>实时聚合发布内容的表现</span></div>
        <section class="metrics">
            <div class="metric"><el-icon><Document /></el-icon><span>文章总数</span><strong>{{ overview.articleCount }}</strong></div>
            <div class="metric amber"><el-icon><EditPen /></el-icon><span>草稿箱</span><strong>{{ overview.draftCount }}</strong></div>
            <div class="metric blue"><el-icon><TrendCharts /></el-icon><span>已发布</span><strong>{{ overview.publishedCount }}</strong></div>
            <div class="metric rose"><el-icon><View /></el-icon><span>累计阅读</span><strong>{{ overview.totalViews }}</strong></div>
        </section>
        <section class="charts"><div class="panel"><h2>近七日发布趋势</h2><div ref="trendElement" class="chart"></div></div><div class="panel"><h2>分类内容分布</h2><div ref="categoryElement" class="chart"></div></div></section>
        <section class="panel hot"><div class="panel-title"><h2>热门文章</h2><span>按公开阅读量排序</span></div><el-table :data="hotArticles"><el-table-column prop="title" label="文章标题" min-width="260"/><el-table-column prop="state" label="状态" width="100"/><el-table-column prop="viewCount" label="阅读量" width="110"/><el-table-column prop="createTime" label="发布时间" width="180"/></el-table></section>
    </div>
</template>

<style scoped lang="scss">
.dashboard-page { min-height: 100%; }.heading { display: flex; justify-content: space-between; align-items: end; margin-bottom: 24px; }.heading p { margin: 0; color: #198456; font-weight: 700; font-size: 12px; letter-spacing: 1px; }.heading h1 { margin: 4px 0 0; font-size: 26px; }.heading > span,.panel-title span { color: #728078; font-size: 13px; }.metrics { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }.metric,.panel { background: #fff; border: 1px solid #e0e8e2; border-radius: 6px; }.metric { display: grid; grid-template-columns: 40px 1fr; gap: 2px 12px; padding: 20px; }.metric .el-icon { grid-row: span 2; display: grid; width: 40px; height: 40px; border-radius: 5px; place-items: center; background: #d9f0e1; color: #147349; font-size: 20px; }.metric span { color: #758077; font-size: 13px; }.metric strong { font-size: 25px; }.amber .el-icon { background: #fff0db; color: #be6c1e; }.blue .el-icon { background: #dcecf4; color: #397a94; }.rose .el-icon { background: #f5e1e7; color: #9a5268; }.charts { display: grid; grid-template-columns: 3fr 2fr; gap: 16px; margin: 16px 0; }.panel { padding: 20px; }.panel h2 { margin: 0; font-size: 16px; }.chart { height: 270px; }.panel-title { display: flex; align-items: baseline; justify-content: space-between; margin-bottom: 14px; }.hot { margin-top: 16px; }@media (max-width: 900px) { .metrics { grid-template-columns: repeat(2, 1fr); }.charts { grid-template-columns: 1fr; } }@media (max-width: 520px) { .heading { align-items: start; flex-direction: column; gap: 8px; }.metrics { grid-template-columns: 1fr; } }
</style>