<script setup>
import { computed, ref, watch } from 'vue'
import DOMPurify from 'dompurify'
import { ArrowLeft, View } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { publicArticleDetailService } from '@/api/article.js'

const route = useRoute()
const router = useRouter()
const article = ref(null)
const loading = ref(false)
const sanitizedContent = computed(() => DOMPurify.sanitize(article.value?.content || ''))
const loadArticle = async () => {
    loading.value = true
    try { article.value = (await publicArticleDetailService(route.params.id)).data } finally { loading.value = false }
}
watch(() => route.params.id, loadArticle, { immediate: true })
</script>

<template>
    <div v-loading="loading" class="detail-page">
        <el-button text :icon="ArrowLeft" @click="router.push('/')">返回文章广场</el-button>
        <article v-if="article" class="article-detail">
            <p class="detail-meta">{{ String(article.createTime).slice(0, 10) }} · <el-icon><View /></el-icon> {{ article.viewCount || 0 }} 次阅读</p>
            <h1>{{ article.title }}</h1>
            <p v-if="article.summary" class="lead">{{ article.summary }}</p>
            <img class="cover" :src="article.coverImg" :alt="article.title" />
            <div class="content" v-html="sanitizedContent"></div>
        </article>
    </div>
</template>

<style scoped lang="scss">
.detail-page { max-width: 820px; margin: 0 auto; }.article-detail { margin-top: 20px; padding: 42px 8px; }.detail-meta { display: flex; align-items: center; gap: 5px; color: #688071; font-size: 13px; }.article-detail h1 { margin: 14px 0 18px; font-family: Georgia, "Noto Serif SC", serif; font-size: 42px; line-height: 1.25; }.lead { padding-left: 18px; border-left: 4px solid #1c8359; color: #52665a; font-size: 18px; line-height: 1.7; }.cover { width: 100%; max-height: 440px; margin: 24px 0; border-radius: 5px; object-fit: cover; }.content { color: #26362c; font-size: 16px; line-height: 1.85; word-break: break-word; }:deep(.content img) { max-width: 100%; height: auto; }:deep(.content pre) { overflow: auto; padding: 16px; background: #edf2ee; }@media (max-width: 600px) { .article-detail { padding-top: 24px; }.article-detail h1 { font-size: 31px; } }
</style>