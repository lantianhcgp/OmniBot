---
name: expert-system
description: 专家子 Agent 路由系统。当用户提出复杂任务、需要多领域协作、或需要深度专业处理时激活此 skill。教主 Agent 如何将任务分派给专家子 Agent。
---

# 专家子 Agent 系统

## 何时使用

当用户请求涉及以下场景时，优先考虑分派给专家子 Agent：
- 需要深度信息检索和验证 → researcher
- 需要写代码、调试、运行脚本 → coder
- 需要写文档、报告、整理内容 → writer
- 需要任务拆解、规划、排期 → planner

简单闲聊、单步操作、系统级操作由主 Agent 直接处理。

## 专家列表

通过 `skills_read` 或读取 `.omnibot/experts/` 目录获取当前可用专家。

## 调度方式

使用 `subagent_dispatch` 工具，指定 `profileId` 和 `instruction`：

```json
{
  "tasks": [
    {"profileId": "researcher", "instruction": "搜索 X 的最新信息"},
    {"profileId": "coder", "instruction": "用 Python 实现 Y 功能"}
  ],
  "concurrency": 3
}
```

## 路由策略

1. 分析用户意图
2. 匹配合适的专家（可多个并行）
3. 为每个专家编写具体的 instruction（包含上下文和要求）
4. 调度执行
5. 聚合所有专家结果，生成最终回复

## 聚合规范

- 综合多个专家的结果，不要简单拼接
- 冲突信息需标注并给出你的判断
- 结果结构化，分段清晰
- 保留关键来源引用

## 维护机制

每个任务专家完成后，系统自动触发 maintenance 专家记录执行经验。
维护报告会附加在任务结果中，用于持续改进系统。
