<script setup>
import { EditPen, HomeFilled } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { useTokenStore } from '@/stores/token.js'

const router = useRouter()
const tokenStore = useTokenStore()
</script>

<template>
    <div class="public-shell">
        <header class="public-header">
            <button class="brand" @click="router.push('/')"><el-icon><HomeFilled /></el-icon>大事件</button>
            <nav>
                <button @click="router.push('/')">文章广场</button>
                <el-button type="primary" :icon="EditPen" @click="router.push(tokenStore.token ? '/admin/dashboard' : '/login')">
                    {{ tokenStore.token ? '进入后台' : '作者登录' }}
                </el-button>
            </nav>
        </header>
        <main class="public-main"><router-view /></main>
        <footer>大事件内容社区 · 发现值得阅读的故事</footer>
    </div>
</template>

<style scoped lang="scss">
.public-shell { min-height: 100vh; background: #f4f7f5; color: #17221d; }
.public-header { height: 68px; display: flex; align-items: center; justify-content: space-between; padding: 0 max(24px, calc((100% - 1160px) / 2)); background: #fff; border-bottom: 1px solid #dce4dd; }
.brand, nav button { border: 0; background: transparent; cursor: pointer; color: inherit; }
.brand { display: flex; align-items: center; gap: 8px; font-weight: 800; font-size: 21px; color: #146b49; }
nav { display: flex; align-items: center; gap: 22px; } nav button { font-size: 14px; }
.public-main { max-width: 1160px; margin: 0 auto; padding: 40px 24px 56px; }
footer { padding: 24px; color: #68766d; text-align: center; font-size: 13px; background: #e9efea; }
@media (max-width: 600px) { .public-header { padding: 0 16px; } .public-main { padding: 24px 16px 40px; } nav { gap: 10px; } }
</style>