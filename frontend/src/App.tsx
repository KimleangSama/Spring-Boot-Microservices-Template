import { useEffect, useState } from 'react';
import './App.css';

interface Product {
  id: number;
  name: string;
  price: number;
}

function App() {
  const [data, setData] = useState<Product[]>([]);
  const title = import.meta.env.VITE_APP_TITLE || 'Default App Title';
  const url = import.meta.env.VITE_API_URL || 'http://localhost:8080';

  useEffect(() => {
    const fetchData = async () => {
      const response = await fetch(`${url}/products/api/product`);
      const data = await response.json();
      setData(data);
    };
    fetchData();
  }, [url]);

  return (
    <>
      <h1>{title}</h1>
      <ul>
        {data.map(item => (
          <li key={item.id}>{item.name}</li>
        ))}
      </ul>
    </>
  )
}

export default App
