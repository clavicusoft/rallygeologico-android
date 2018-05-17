package com.rallygeologico;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Clase para manejar el control del paginador en las opciones del usuario
 */
class PagerAdapter extends FragmentPagerAdapter {

    private static int NUM_PAGES = 2;

    Context mContext;
    public PagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    /**
     * Maneja las opciones del menu y para cada opcion realiza las
     * instrucciones que se le indican
     * @param position
     * @return
     */
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        Bundle args;
        switch (position) {
            //Carga el fragmento de la lista de rallies del usuario
            case 0:
                fragment = new RallyListFragment();
                args = new Bundle();
                fragment.setArguments(args);
                return fragment;
            //Carga el fragmento de los logros obtenidos por el usuario
            case 1:
                fragment = new AchievementListFragment();
                args = new Bundle();
                fragment.setArguments(args);
                return fragment;
            default:
                return null;
        }
    }

    /**
     * Obtiene el numero de paginas del paginador
     * @return Entero con el numero de paginas
     */
    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    /**
     * Obtiene el titulo de cada pagina
     * @param position Entero con la posicion seleccionada
     * @return String con el nombre de la pagina actual
     */
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Recorridos";
            case 1:
                return "Logros";
        }
        return null;
    }
}
