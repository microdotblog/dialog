package com.dialogapp.dialog.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.dialogapp.dialog.ui.common.ListViewModel;
import com.dialogapp.dialog.ui.common.RequestViewModel;
import com.dialogapp.dialog.ui.loginscreen.LoginViewModel;
import com.dialogapp.dialog.ui.profilescreen.AccountViewModel;
import com.dialogapp.dialog.ui.profilescreen.ProfileViewModel;
import com.dialogapp.dialog.util.MicroblogViewModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Binds each specified view model into a map of providers that the factory can use
 */

@Module
abstract class ViewModelModule {
    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(MicroblogViewModelFactory factory);

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    abstract ViewModel bindLoginViewModel(LoginViewModel loginViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ListViewModel.class)
    abstract ViewModel bindListViewModel(ListViewModel ListViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel.class)
    abstract ViewModel bindAccountViewModel(AccountViewModel accountViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel.class)
    abstract ViewModel bindProfileViewModel(ProfileViewModel profileViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(RequestViewModel.class)
    abstract ViewModel bindRequestViewModel(RequestViewModel requestViewModel);
}
