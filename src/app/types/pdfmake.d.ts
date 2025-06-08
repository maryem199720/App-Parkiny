declare module 'pdfmake' {
  const pdfMake: {
    vfs: any;
    createPdf(docDefinition: any, tableLayouts?: any): any;
    // Ajoutez d'autres méthodes si nécessaires (ex. download)
  };
  export default pdfMake;
}

declare module 'pdfmake/build/vfs_fonts' {
  const vfsFonts: any;
  export default vfsFonts;
}