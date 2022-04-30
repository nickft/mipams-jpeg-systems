import './App.css';

import { BrowserRouter, Route, Routes } from "react-router-dom";

import Home from './components/Home';
import PageNotFound from './components/PageNotFound';

import Parse from './containers/Parse';
import Layout from './components/Layout';

import { createTheme, ThemeProvider, responsiveFontSizes } from '@mui/material/styles';

let theme = createTheme();
theme = responsiveFontSizes(theme);

function App() {
  return (
    <ThemeProvider theme={theme}>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Layout />}>
            <Route index element={<Home />} />
            <Route path="parse" element={<Parse />} />
            <Route path="*" element={<PageNotFound />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </ThemeProvider>

  );
}

export default App;
