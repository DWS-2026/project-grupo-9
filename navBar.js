let bar = document.getElementById('navigationBar');

bar.innerHTML = `<nav class="navbar navbar-expand-lg navbar-light" style="background-color: #dfc797;">
                    <div class="container px-4 px-lg-5">
                        <a class="navbar-brand fw-bold" href="#!">
                            BOMBOMB
                        </a>
                        <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
                            data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent"
                            aria-expanded="false" aria-label="Toggle navigation"><span
                            class="navbar-toggler-icon"></span></button>
                        <div class="collapse navbar-collapse" id="navbarSupportedContent">
                            <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-lg-4">
                                <li class="nav-item fw-bold"><a class="nav-link" aria-current="page" href="./index.html">INICIO</a>
                                </li>
                                <li class="nav-item fw-bold"><a class="nav-link" href="./productsPage.html">PRODUCTOS</a></li>
                                <li class="nav-item fw-bold"><a class="nav-link" href="./customBox.html">PERSONALIZAR</a></li>
                                <li class="nav-item fw-bold"><a class="nav-link" href="./admin.html">ADMIN</a></li>
                            </ul>
                            <a class="btn btn-outline-dark me-1" href="./cart.html">
                                <i class="bi-cart-fill me-1"></i>
                                Carrito
                                <span class="badge bg-dark text-white ms-1 rounded-pill">0</span>
                            </a>
                            <a class="btn btn-outline-dark me-1" href="./profilePage.html">
                                <i class="bi-person-fill me-1"></i>
                                Perfil
                            </a>
                        </div>
                    </div>
                </nav>`;