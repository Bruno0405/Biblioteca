import { defineMermaidSetup } from '@slidev/types'

export default defineMermaidSetup(() => {
  return {
    theme: 'base',
    themeVariables: {
      darkMode: true,
      background: '#0e1017',

      primaryColor: '#1a1f2e',
      primaryTextColor: '#e8edf2',
      primaryBorderColor: '#2eafc9',
      lineColor: '#2eafc9',

      secondaryColor: '#141824',
      tertiaryColor: '#222838',

      textColor: '#e8edf2',
      classText: '#e8edf2',

      mainBkg: '#1a1f2e',
      nodeBorder: '#2eafc9',

      clusterBkg: '#141824',
      clusterBorder: '#1a8aa8',
      titleColor: '#5fc3d9',
      edgeLabelBackground: '#1a1f2e',
      nodeTextColor: '#e8edf2',

      noteBkgColor: '#1a1f2e',
      noteTextColor: '#e8edf2',
      noteBorderColor: '#2eafc9',
    },
  }
})
