import request from '@/utils/request.js'
import { useTokenStore } from '@/stores/token.js'
//文章分类列表查询
export const articleCategoryListService = ()=>{
    //const tokenStore = useTokenStore();
    //在pinia中定义的响应式数据,都不需要.value
    //return request.get('/category',{headers:{'Authorization':tokenStore.token}})
    return request.get('/category')
}

//新增文章分类
export const articleCategoryAddService = (categoryData) => {
  return request.post('/category', categoryData)
}

//编辑文章分类
export const articleCategoryUpdateService = (categoryData) => {
  return request.put('/category', categoryData)
}

//文章分类删除
export const articleCategoryDeleteService=(id)=>{
  return request.delete('/category?id='+id)
}

//文章列表查询
export const articleListService=(params)=>{
  return request.get('/article',{params:params})
}

//文章添加
export const articleAddService=(articleData)=>{
  return request.post('/article',articleData);
}

//文章详情
export const articleDetailService=(id)=>{
  return request.get('/article/detail',{params:{id}})
}

//文章更新
export const articleUpdateService=(articleData)=>{
  return request.put('/article',articleData)
}

// 根据文章正文生成摘要
export const articleSummaryService = (content) => {
  return request.post('/article/ai/summary', { content })
}

export const articleAssistantService = (articleId, content) => {
  return request.post('/article/ai/assistant', { articleId: articleId || null, content })
}

//文章删除
export const articleDeleteService=(id)=>{
  return request.delete('/article?id='+id)
}

export const publicArticleListService = (params) => request.get('/public/articles', { params })

export const publicArticleDetailService = (id) => request.get(`/public/articles/${id}`)

export const dashboardOverviewService = () => request.get('/dashboard/overview')

export const dashboardPublishTrendService = () => request.get('/dashboard/publish-trend')

export const dashboardCategoryDistributionService = () => request.get('/dashboard/category-distribution')

export const dashboardHotArticlesService = () => request.get('/dashboard/hot-articles')