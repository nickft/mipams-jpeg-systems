import React from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { createTheme, ThemeProvider, responsiveFontSizes } from '@mui/material/styles';
import Layout from "./components/Layout";

import Home from './components/Home';
import PageNotFound from './components/PageNotFound';
import Parse from './containers/Parse';
import Generate from './containers/Generate';

let theme = createTheme();
theme = responsiveFontSizes(theme);

function App() {

  return (
    <ThemeProvider theme={theme}>
      <BrowserRouter basename='/jumbf'>
        <Routes>
          <Route path="/" element={<Layout />}>
            <Route index element={<Home />} />
            <Route path="parse" element={<Parse />} />
            <Route path="generate" element={<Generate />} />
            <Route path="*" element={<PageNotFound />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </ThemeProvider>
  );
}

export default App;
