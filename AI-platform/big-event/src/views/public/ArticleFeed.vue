<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, View } from '@element-plus/icons-vue'
import { publicArticleListService } from '@/api/article.js'

const router = useRouter()
const articles = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(9)
const keyword = ref('')
const loading = ref(false)

const loadArticles = async () => {
    loading.value = true
    try {
        const result = await publicArticleListService({ pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value || null })
        articles.value = result.data.items
        total.value = result.data.total
    } finally { loading.value = false }
}

const search = () => { pageNum.value = 1; loadArticles() }
const formatDate = (value) => value ? String(value).slice(0, 10) : ''
const plainSummary = (article) => (article.summary || article.content || '').replace(/<[^>]*>/g, '').replace(/&nbsp;/gi, ' ').trim()
onMounted(loadArticles)
</script>

<template>
    <section class="feed-heading">
        <p class="eyebrow">CONTENT SQUARE</p>
        <h1>读一点新的东西</h1>
        <p>来自创作者的公开文章与思考，按最新发布排序。</p>
        <div class="search-bar">
            <el-input v-model="keyword" placeholder="搜索标题或摘要" clearable @keyup.enter="search"><template #prefix><el-icon><Search /></el-icon></template></el-input>
            <el-button type="primary" @click="search">搜索文章</el-button>
        </div>
    </section>
    <section v-loading="loading" class="article-grid">
        <article v-for="article in articles" :key="article.id" class="article-card" @click="router.push(`/articles/${article.id}`)">
            <img :src="article.coverImg" :alt="article.title" />
            <div class="article-copy">
                <p class="meta">{{ formatDate(article.createTime) }}</p>
                <h2>{{ article.title }}</h2>
                <p class="summary">{{ plainSummary(article) || '这篇文章暂未填写摘要。' }}</p>
                <span class="views"><el-icon><View /></el-icon>{{ article.viewCount || 0 }} 次阅读</span>
            </div>
        </article>
        <el-empty v-if="!loading && articles.length === 0" description="暂时没有匹配的公开文章" />
    </section>
    <el-pagination v-if="total > pageSize" v-model:current-page="pageNum" :page-size="pageSize" layout="prev, pager, next" background :total="total" @current-change="loadArticles" />
</template>

<style scoped lang="scss">
.feed-heading { padding: 34px 0 36px; border-bottom: 1px solid #cad6cd; }
.eyebrow { margin: 0 0 8px; color: #168054; font-size: 12px; font-weight: 800; letter-spacing: 1.4px; }
h1 { margin: 0; font-family: Georgia, "Noto Serif SC", serif; font-size: 42px; font-weight: 700; } .feed-heading > p:not(.eyebrow) { color: #5c6d62; }
.search-bar { display: flex; max-width: 560px; margin-top: 24px; gap: 10px; }
.article-grid { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 20px; padding: 30px 0; min-height: 260px; }
.article-card { overflow: hidden; background: #fff; border: 1px solid #dce5de; border-radius: 6px; cursor: pointer; transition: transform .2s, box-shadow .2s; } .article-card:hover { transform: translateY(-4px); box-shadow: 0 12px 22px rgba(22, 67, 43, .12); }
.article-card img { width: 100%; height: 170px; object-fit: cover; background: #dce6df; }.article-copy { padding: 18px; }.meta,.views { color: #6d7d72; font-size: 12px; }.meta { margin: 0 0 8px; }.article-copy h2 { margin: 0; font-size: 18px; }.summary { display: -webkit-box; overflow: hidden; color: #536158; font-size: 14px; line-height: 1.6; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }.views { display: flex; align-items: center; gap: 4px; }.el-pagination { justify-content: center; }
@media (max-width: 800px) { .article-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); } } @media (max-width: 520px) { h1 { font-size: 34px; }.article-grid { grid-template-columns: 1fr; }.search-bar { flex-direction: column; } }
</style>