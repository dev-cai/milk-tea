// Minimal region data stub to satisfy imports
// Expand this dataset or replace with real data as needed

export const regionData = [
  {
    name: '广东省',
    code: '440000',
    children: [
      {
        name: '深圳市',
        code: '440300',
        children: [
          { name: '南山区', code: '440305' },
          { name: '福田区', code: '440304' },
          { name: '罗湖区', code: '440303' }
        ]
      },
      {
        name: '广州市',
        code: '440100',
        children: [
          { name: '天河区', code: '440106' },
          { name: '越秀区', code: '440104' }
        ]
      }
    ]
  },
  {
    name: '北京市',
    code: '110000',
    children: [
      {
        name: '市辖区',
        code: '110100',
        children: [
          { name: '朝阳区', code: '110105' },
          { name: '海淀区', code: '110108' }
        ]
      }
    ]
  },
  {
    name: '上海市',
    code: '310000',
    children: [
      {
        name: '市辖区',
        code: '310100',
        children: [
          { name: '浦东新区', code: '310115' },
          { name: '黄浦区', code: '310101' }
        ]
      }
    ]
  }
]
